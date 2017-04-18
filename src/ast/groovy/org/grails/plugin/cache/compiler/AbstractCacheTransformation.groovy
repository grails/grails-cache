/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.plugin.cache.compiler

import grails.artefact.Controller
import grails.compiler.ast.GrailsArtefactClassInjector
import grails.plugin.cache.GrailsCacheKeyGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.sc.transformers.CompareToNullExpression
import org.grails.compiler.injection.GrailsASTUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import static org.codehaus.groovy.ast.ClassHelper.*
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
import java.lang.reflect.Modifier

import static org.grails.compiler.injection.GrailsASTUtils.copyParameters
import static org.grails.compiler.injection.GrailsASTUtils.processVariableScopes

/**
 * @since 4.0.0
 */
@CompileStatic
abstract class AbstractCacheTransformation implements ASTTransformation {

    public static final ClassNode COMPILE_STATIC_TYPE = ClassHelper.make(CompileStatic)
    public static final ClassNode TYPE_CHECKED_TYPE = ClassHelper.make(TypeChecked)
    public static final ClassNode OBJECT_TYPE = ClassHelper.make(Object)

    public static final String GRAILS_CACHE_MANAGER_PROPERTY_NAME = 'grailsCacheManager'
    public static final String CACHE_KEY_LOCAL_VARIABLE_NAME = '$_cache_cacheKey'
    public static final String METHOD_PARAMETER_MAP_LOCAL_VARIABLE_NAME = '$_method_parameter_map'
    public static final String CACHE_VARIABLE_LOCAL_VARIABLE_NAME = '$_cache_cacheVariable'
    public static final String GRAILS_CACHE_KEY_GENERATOR_PROPERTY_NAME = 'customCacheKeyGenerator'

    @Override
    void visit(final ASTNode[] astNodes, final SourceUnit sourceUnit) {
        final ASTNode firstNode = astNodes[0]
        final ASTNode secondNode = astNodes[1]
        if (!(firstNode instanceof AnnotationNode) || !(secondNode instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: " + firstNode.getClass().getName() +
                    " / " + secondNode.getClass().getName())
        }

        final AnnotationNode grailsCacheAnnotationNode = (AnnotationNode) firstNode
        final AnnotatedNode annotatedNode = (AnnotatedNode) secondNode

        if (annotatedNode instanceof MethodNode) {
            MethodNode methodNode = (MethodNode) annotatedNode
            ClassNode declaringClass = methodNode.getDeclaringClass()

            prohibitControllerClasses declaringClass, sourceUnit, grailsCacheAnnotationNode

            configureCachingForMethod(declaringClass, grailsCacheAnnotationNode, methodNode, sourceUnit)
            addAutowiredPropertyToClass declaringClass, CacheManager, GRAILS_CACHE_MANAGER_PROPERTY_NAME
        } else if(annotatedNode instanceof ClassNode) {
            ClassNode annotatedClass = (ClassNode)annotatedNode

            prohibitControllerClasses annotatedClass, sourceUnit, grailsCacheAnnotationNode

            addAutowiredPropertyToClass annotatedClass, CacheManager, GRAILS_CACHE_MANAGER_PROPERTY_NAME

            List<MethodNode> declaredMethods = annotatedClass.allDeclaredMethods

            for(MethodNode method : declaredMethods) {
                if(shouldMethodBeConfiguredForCaching(method)) {
                    configureCachingForMethod annotatedClass, grailsCacheAnnotationNode, method, sourceUnit
                }
            }
        }
    }

    protected void prohibitControllerClasses(ClassNode declaringClass, SourceUnit sourceUnit, AnnotationNode cacheAnnotationNode) {
        if (GrailsASTUtils.isSubclassOfOrImplementsInterface(declaringClass, ClassHelper.make(Controller))) {
            GrailsASTUtils.error(sourceUnit, cacheAnnotationNode, "The ${cacheAnnotationNode.classNode.name} Annotation Is Not Supported In A Controller.")
        }
    }

    protected boolean shouldMethodBeConfiguredForCaching(MethodNode method) {
        method.isPublic()
    }

    abstract
    protected void configureCachingForMethod(MethodNode methodToCache, ClassNode declaringClass, AnnotationNode cacheAnnotationOnMethod, BlockStatement cachingCode, Expression expressionToCallOriginalMethod, SourceUnit sourceUnit)

    protected void configureCachingForMethod(ClassNode declaringClass, AnnotationNode cacheAnnotationOnMethod, MethodNode methodToCache, SourceUnit sourceUnit) {
        Expression expressionToCallOriginalMethod = moveOriginalCodeToNewMethod(sourceUnit, declaringClass, methodToCache)

        BlockStatement cachingCode = new BlockStatement()

        addCodeToExecuteIfCacheManagerIsNull(expressionToCallOriginalMethod, cachingCode)

        if(requiresParameterMap()) {
            declareAndInitializeParameterValueMap(cachingCode, methodToCache)
        }

        configureCachingForMethod(methodToCache, declaringClass, cacheAnnotationOnMethod, cachingCode, expressionToCallOriginalMethod, sourceUnit)
    }

    protected boolean requiresParameterMap() {
        true
    }

    protected addAutowiredPropertyToClass(ClassNode classNode, Class propertyType, String propertyName) {
        if (!classNode.hasProperty(propertyName)) {
            FieldNode cacheManagerFieldNode = new FieldNode(propertyName, Modifier.PRIVATE, ClassHelper.make(propertyType), classNode, new EmptyExpression())
            AnnotationNode autowiredAnnotationNode = new AnnotationNode(ClassHelper.make(Autowired))
            autowiredAnnotationNode.setMember('required', new ConstantExpression(false))
            cacheManagerFieldNode.addAnnotation(autowiredAnnotationNode)
            PropertyNode cacheManagerPropertyNode = new PropertyNode(cacheManagerFieldNode, Modifier.PUBLIC, null, null)
            classNode.addProperty(cacheManagerPropertyNode)
        }
    }

    protected MethodCallExpression moveOriginalCodeToNewMethod(SourceUnit source, ClassNode classNode, MethodNode methodNode) {
        String renamedMethodName = '$_cache_' + methodNode.getName()
        def newParameters = methodNode.getParameters() ? (copyParameters(((methodNode.getParameters() as List)) as Parameter[])) : new Parameter[0]

        MethodNode renamedMethodNode = new MethodNode(
                renamedMethodName,
                Modifier.PROTECTED, methodNode.getReturnType().getPlainNodeReference(),
                newParameters,
                GrailsArtefactClassInjector.EMPTY_CLASS_ARRAY,
                methodNode.code
        )

        // GrailsCompileStatic and GrailsTypeChecked are not explicitly addressed
        // here but they will be picked up because they are @AnnotationCollector annotations
        // which use CompileStatic and TypeChecked...
        renamedMethodNode.addAnnotations(methodNode.getAnnotations(COMPILE_STATIC_TYPE))
        renamedMethodNode.addAnnotations(methodNode.getAnnotations(TYPE_CHECKED_TYPE))

        methodNode.setCode(null)
        classNode.addMethod(renamedMethodNode)

        processVariableScopes(source, classNode, renamedMethodNode)

        final originalMethodCall = new MethodCallExpression(new VariableExpression("this"), renamedMethodName, new ArgumentListExpression(renamedMethodNode.parameters))
        originalMethodCall.setImplicitThis(false)
        originalMethodCall.setMethodTarget(renamedMethodNode)

        originalMethodCall
    }

    protected void addCodeToExecuteIfCacheManagerIsNull(Expression expressionToCallOriginalMethod, BlockStatement codeBlock) {
        VariableExpression cacheManagerVariableExpression = new VariableExpression(GRAILS_CACHE_MANAGER_PROPERTY_NAME)
        Expression cacheManagerNotNullExpression = new CompareToNullExpression(cacheManagerVariableExpression, false)
        Statement ifCacheManager = new IfStatement(new BooleanExpression(cacheManagerNotNullExpression), new EmptyStatement(), new ReturnStatement(expressionToCallOriginalMethod))

        codeBlock.addStatement(ifCacheManager)
    }

    protected void addCodeToRetrieveCache(AnnotationNode cacheAnnotationOnMethod, BlockStatement codeBlock) {
        VariableExpression cacheManagerVariableExpression = new VariableExpression(GRAILS_CACHE_MANAGER_PROPERTY_NAME)
        Expression cacheVariableExpression = new VariableExpression(CACHE_VARIABLE_LOCAL_VARIABLE_NAME)
        Expression cacheNameExpression = (ConstantExpression) cacheAnnotationOnMethod.getMember('value')
        Expression getCacheMethodCallExpression = new MethodCallExpression(cacheManagerVariableExpression, 'getCache', new ArgumentListExpression(cacheNameExpression))
        MethodNode getCacheMethod = ClassHelper.make(CacheManager).getMethod('getCache', [new Parameter(STRING_TYPE, 'name')] as Parameter[])
        getCacheMethodCallExpression.methodTarget = getCacheMethod
        Expression declareCacheExpression = new DeclarationExpression(cacheVariableExpression, Token.newSymbol(Types.EQUALS, 0, 0), getCacheMethodCallExpression)

        codeBlock.addStatement(new ExpressionStatement(declareCacheExpression))
    }

    protected void addCodeToInitializeCacheKey(ClassNode declaringClass, MethodNode methodToCache, AnnotationNode cacheAnnotationOnMethod, BlockStatement codeBlock) {
        addAutowiredPropertyToClass declaringClass, GrailsCacheKeyGenerator, GRAILS_CACHE_KEY_GENERATOR_PROPERTY_NAME

        ArgumentListExpression createKeyArgs = new ArgumentListExpression()
        createKeyArgs.addExpression(constX(declaringClass.getName()))
        createKeyArgs.addExpression(constX(methodToCache.getName()))
        createKeyArgs.addExpression(callX(varX('this'), 'hashCode', new ArgumentListExpression()))



        Expression keyGenMember = cacheAnnotationOnMethod.getMember('key')
        MethodNode generateMethod

        if(keyGenMember instanceof ClosureExpression) {
            ClosureExpression closureExpression = (ClosureExpression)keyGenMember
            cacheAnnotationOnMethod.members.remove('key')
            makeClosureParameterAware(declaringClass.module.context, methodToCache, closureExpression)

            generateMethod = make(GrailsCacheKeyGenerator).getMethod('generateFromClosure', params(
                                                                                        param(STRING_TYPE, 'className'),
                                                                                        param(STRING_TYPE, 'methodName'),
                                                                                        param(int_TYPE, 'objHashCode'),
                                                                                        param(CLOSURE_TYPE, 'keyGenerator')))
            createKeyArgs.addExpression(keyGenMember)
        }
        else {
            generateMethod = make(GrailsCacheKeyGenerator).getMethod('generateFromParameters', params(
                    param(STRING_TYPE, 'className'),
                    param(STRING_TYPE, 'methodName'),
                    param(int_TYPE, 'objHashCode'),
                    param(MAP_TYPE, 'methodParams')))
            createKeyArgs.addExpression(varX(METHOD_PARAMETER_MAP_LOCAL_VARIABLE_NAME))
        }
        MethodCallExpression cacheKeyExpression = new MethodCallExpression(new VariableExpression(GRAILS_CACHE_KEY_GENERATOR_PROPERTY_NAME), generateMethod.name, createKeyArgs)

        cacheKeyExpression.methodTarget = generateMethod
        VariableExpression cacheKeyVariableExpression = new VariableExpression(CACHE_KEY_LOCAL_VARIABLE_NAME)
        DeclarationExpression cacheKeyDeclaration = new DeclarationExpression(cacheKeyVariableExpression, Token.newSymbol(Types.EQUALS, 0, 0), cacheKeyExpression)
        codeBlock.addStatement(new ExpressionStatement(cacheKeyDeclaration))
    }

    protected void declareAndInitializeParameterValueMap(BlockStatement codeBlock, MethodNode methodToCache) {
        def declareMap = new DeclarationExpression(new VariableExpression(METHOD_PARAMETER_MAP_LOCAL_VARIABLE_NAME, MAP_TYPE), Token.newSymbol(Types.EQUALS, 0, 0), new ConstructorCallExpression(ClassHelper.make(LinkedHashMap), new ArgumentListExpression()))
        codeBlock.addStatement(new ExpressionStatement(declareMap))
        def parameters1 = methodToCache.getParameters()
        if (parameters1) {
            MethodNode mapPutMethod = MAP_TYPE.getMethod('put', [new Parameter(OBJECT_TYPE, 'key'), new Parameter(OBJECT_TYPE, 'value')] as Parameter[])
            for (Parameter p : parameters1) {
                ArgumentListExpression putArgs = new ArgumentListExpression()
                putArgs.addExpression(new ConstantExpression(p.getName()))
                putArgs.addExpression(new VariableExpression(p.getName()))
                MethodCallExpression mce = new MethodCallExpression(new VariableExpression(METHOD_PARAMETER_MAP_LOCAL_VARIABLE_NAME), 'put', putArgs)
                mce.methodTarget = mapPutMethod
                codeBlock.addStatement(new ExpressionStatement(mce))
            }
        }
    }

    protected void makeClosureParameterAware(SourceUnit sourceUnit, MethodNode method, ClosureExpression closureExpression) {
        VariableScope variableScope = closureExpression.variableScope
        for (p in method.parameters) {
            if (variableScope.isReferencedClassVariable(p.name)) {
                variableScope.removeReferencedClassVariable(p.name)
            }
            variableScope.putDeclaredVariable(p)
            variableScope.putReferencedLocalVariable(p)
            p.setClosureSharedVariable(true)
        }
        new ClassCodeExpressionTransformer() {
            @Override
            Expression transform(Expression exp) {
                if (exp instanceof VariableExpression) {
                    Variable var = ((VariableExpression) exp).accessedVariable
                    if (var instanceof DynamicVariable) {
                        Variable ref = variableScope.getDeclaredVariable(var.name)
                        if (ref != null) {
                            def newExpr = new VariableExpression(ref)
                            newExpr.setClosureSharedVariable(true)
                            newExpr.setAccessedVariable(ref)

                            return newExpr
                        }
                    }
                }
                return super.transform(exp)
            }

            @Override
            protected SourceUnit getSourceUnit() {
                return sourceUnit
            }
        }.visitClosureExpression(closureExpression)
    }
}
