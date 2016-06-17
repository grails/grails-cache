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
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
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
class CachePutTransformation extends AbstractCacheTransformation {

    @Override
    protected void configureCachingForMethod(ClassNode declaringClass, AnnotationNode cacheAnnotationOnMethod, MethodNode methodToCache, SourceUnit sourceUnit) {
        Expression expressionToCallOriginalMethod = moveOriginalCodeToNewMethod(sourceUnit, declaringClass, methodToCache)

        BlockStatement cachingCode = new BlockStatement()

        addCodeToExecuteIfCacheManagerIsNull(expressionToCallOriginalMethod, cachingCode)
        addCodeToRetrieveCache(cacheAnnotationOnMethod, cachingCode)
        addCodeToInitializeCacheKey(declaringClass, methodToCache, cacheAnnotationOnMethod, cachingCode)

        Expression getReturnValue = new DeclarationExpression(new VariableExpression('$_cache_originalMethodReturnValue'), Token.newSymbol(Types.EQUALS, 0, 0), expressionToCallOriginalMethod)
        cachingCode.addStatement(new ExpressionStatement(getReturnValue))
        ArgumentListExpression putArgs = new ArgumentListExpression()
        putArgs.addExpression(new VariableExpression(CACHE_KEY_LOCAL_VARIABLE_NAME))
        putArgs.addExpression(new VariableExpression('$_cache_originalMethodReturnValue'))
        Expression updateCacheExpression = new MethodCallExpression(new VariableExpression(CACHE_VARIABLE_LOCAL_VARIABLE_NAME), 'put', putArgs)
        cachingCode.addStatement(new ExpressionStatement(updateCacheExpression))
        cachingCode.addStatement(new ReturnStatement(new VariableExpression('$_cache_originalMethodReturnValue')))

        methodToCache.code = cachingCode
    }
}
