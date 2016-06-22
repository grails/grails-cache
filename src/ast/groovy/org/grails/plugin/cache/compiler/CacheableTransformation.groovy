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

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * @since 4.0.0
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
@CompileStatic
public class CacheableTransformation extends AbstractCacheTransformation {

    public static final String CACHE_VALUE_WRAPPER_LOCAL_VARIABLE_NAME = '$_cache_valueWrapper'
    public static final String CACHE_ORIGINAL_METHOD_RETURN_VALUE_LOCAL_VARIABLE_NAME = '$_cache_originalMethodReturnValue'

    protected void configureCachingForMethod(ClassNode declaringClass, AnnotationNode cacheAnnotationOnMethod, MethodNode methodToCache, SourceUnit sourceUnit) {
        Expression expressionToCallOriginalMethod = moveOriginalCodeToNewMethod(sourceUnit, declaringClass, methodToCache)

        BlockStatement cachingCode = new BlockStatement()

        addCodeToExecuteIfCacheManagerIsNull(expressionToCallOriginalMethod, cachingCode)
        addCodeToRetrieveCache(cacheAnnotationOnMethod, cachingCode)
        addCodeToInitializeCacheKey(methodToCache, cacheAnnotationOnMethod, cachingCode)
        addCodeToRetrieveWrapperFromCache(cachingCode)

        Expression valueWrapperVariableExpression = new VariableExpression(CACHE_VALUE_WRAPPER_LOCAL_VARIABLE_NAME)
        BlockStatement wrapperNotNullBlock = getCodeToExecuteIfWrapperExistsInCache()
        BlockStatement wrapperIsNullBlock = getCodeToExecuteIfWrapperIsNull(expressionToCallOriginalMethod)

        Statement ifValueWrapperStatement = new IfStatement(new BooleanExpression(valueWrapperVariableExpression), wrapperNotNullBlock, wrapperIsNullBlock)
        cachingCode.addStatement(ifValueWrapperStatement)

        methodToCache.code = cachingCode
    }

    protected BlockStatement getCodeToExecuteIfWrapperExistsInCache() {
        BlockStatement wrapperNotNullBlock = new BlockStatement()
        Expression valueWrapperVariableExpression = new VariableExpression(CACHE_VALUE_WRAPPER_LOCAL_VARIABLE_NAME)
        Expression getValueFromWrapperMethodCallExpression = new MethodCallExpression(valueWrapperVariableExpression, 'get', new ArgumentListExpression())
        wrapperNotNullBlock.addStatement(new ReturnStatement(getValueFromWrapperMethodCallExpression))
        wrapperNotNullBlock
    }

    protected void addCodeToRetrieveWrapperFromCache(BlockStatement codeBlock) {
        VariableExpression cacheKeyVariableExpression = new VariableExpression(CACHE_KEY_LOCAL_VARIABLE_NAME)
        Expression cacheVariableExpression = new VariableExpression(CACHE_VARIABLE_LOCAL_VARIABLE_NAME)
        Expression getValueWrapperMethodCallExpression = new MethodCallExpression(cacheVariableExpression, 'get', cacheKeyVariableExpression)
        Expression valueWrapperVariableExpression = new VariableExpression(CACHE_VALUE_WRAPPER_LOCAL_VARIABLE_NAME)
        Expression declareValueWrapperExpression = new DeclarationExpression(valueWrapperVariableExpression, Token.newSymbol(Types.EQUALS, 0, 0), getValueWrapperMethodCallExpression)

        codeBlock.addStatement(new ExpressionStatement(declareValueWrapperExpression))
    }

    protected BlockStatement getCodeToExecuteIfWrapperIsNull(MethodCallExpression expressionToCallOriginalMethod) {
        BlockStatement wrapperIsNullBlock = new BlockStatement()

        Expression cacheKeyVariableExpression = new VariableExpression(CACHE_KEY_LOCAL_VARIABLE_NAME)
        Expression cacheVariableExpression = new VariableExpression(CACHE_VARIABLE_LOCAL_VARIABLE_NAME)
        Expression returnValueVariableExpression = new VariableExpression(CACHE_ORIGINAL_METHOD_RETURN_VALUE_LOCAL_VARIABLE_NAME)
        Expression initializeReturnValueExpression = new DeclarationExpression(returnValueVariableExpression, Token.newSymbol(Types.EQUALS, 0, 0), expressionToCallOriginalMethod)
        ArgumentListExpression putArgs = new ArgumentListExpression()
        putArgs.addExpression(cacheKeyVariableExpression)
        putArgs.addExpression(returnValueVariableExpression)
        Expression updateCache = new MethodCallExpression(cacheVariableExpression, 'put', putArgs)
        wrapperIsNullBlock.addStatement(new ExpressionStatement(initializeReturnValueExpression))
        wrapperIsNullBlock.addStatement(new ExpressionStatement(updateCache))
        wrapperIsNullBlock.addStatement(new ReturnStatement(returnValueVariableExpression))
        wrapperIsNullBlock
    }

}
