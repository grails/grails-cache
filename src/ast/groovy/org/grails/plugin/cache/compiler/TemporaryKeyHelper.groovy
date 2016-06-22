package org.grails.plugin.cache.compiler

import groovy.transform.CompileStatic
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * This is broken and just a placeholder for now.
 */
@CompileStatic
class TemporaryKeyHelper {

    static createKey(Map methodParams, String spel) {

        List params = []

        if (spel) {
            def context = new StandardEvaluationContext()
            context.variables = methodParams

            def parser = new SpelExpressionParser()
            def expression = parser.parseExpression(spel)

            def value = expression.getValue(context)
            params << value
        } else {
            if (methodParams) {
                params << methodParams
            }
        }

        new TemporaryGrailsCacheKey(params)
    }
}

class TemporaryGrailsCacheKey {

    private final Object[] params;

    public TemporaryGrailsCacheKey(Object... elements) {
        Assert.notNull(elements, "Elements must not be null")
        this.params = new Object[elements.length]
        System.arraycopy(elements, 0, this.params, 0, elements.length)
    }

    @Override
    boolean equals(Object obj) {
        (this.is(obj) || (obj instanceof TemporaryGrailsCacheKey && Arrays.deepEquals(this.params, ((TemporaryGrailsCacheKey) obj).params)))
    }

    @Override
    int hashCode() {
        Arrays.deepHashCode(this.params)
    }

    @Override
    public String toString() {
        "TemporaryGrailsCacheKey [${StringUtils.arrayToCommaDelimitedString(this.params)}]";
    }
}
