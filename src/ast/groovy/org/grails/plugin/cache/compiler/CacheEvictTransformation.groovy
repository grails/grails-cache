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
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * @since 4.0.0
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
@CompileStatic
class CacheEvictTransformation extends AbstractCacheTransformation {

    @Override
    protected void configureCachingForMethod(MethodNode methodToCache, ClassNode declaringClass, AnnotationNode cacheAnnotationOnMethod, BlockStatement cachingCode, Expression expressionToCallOriginalMethod, SourceUnit sourceUnit) {
        addCodeToRetrieveCache(cacheAnnotationOnMethod, cachingCode)

        Expression clearCacheMethodCall = new MethodCallExpression(new VariableExpression(CACHE_VARIABLE_LOCAL_VARIABLE_NAME), 'clear', new ArgumentListExpression())

        cachingCode.addStatement(new ExpressionStatement(clearCacheMethodCall))
        cachingCode.addStatement(new ReturnStatement(expressionToCallOriginalMethod))

        methodToCache.code = cachingCode
    }
}
