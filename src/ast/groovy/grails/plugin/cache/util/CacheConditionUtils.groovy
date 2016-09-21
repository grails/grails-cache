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
package grails.plugin.cache.util

import groovy.transform.CompileStatic
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * @since 4.0.0
 */
@CompileStatic
class CacheConditionUtils {

    static boolean shouldCache(instance, Map methodParams, String spel) {
        def context = new StandardEvaluationContext(instance)
        context.variables = methodParams

        def parser = new SpelExpressionParser()
        def expression = parser.parseExpression(spel)

        expression.getValue(context)
    }
}
