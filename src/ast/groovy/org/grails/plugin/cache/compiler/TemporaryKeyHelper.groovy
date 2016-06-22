package org.grails.plugin.cache.compiler

import groovy.transform.CompileStatic
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * This is broken and just a placeholder for now.
 */
@CompileStatic
class TemporaryKeyHelper {

    static createKey(String className, String methodName, int objHashCode, Map methodParams, String spel) {

        def simpleKey

        if (spel) {
            def context = new StandardEvaluationContext()
            context.variables = methodParams

            def parser = new SpelExpressionParser()
            def expression = parser.parseExpression(spel)

            simpleKey = expression.getValue(context)
        } else {
            simpleKey = methodParams
        }

        new TemporaryGrailsCacheKey(className, methodName, objHashCode, simpleKey)
    }
}

/**
 * A copy of CustomCacheKeyGenerator.CacheKey for now...
 */
class TemporaryGrailsCacheKey implements Serializable {
    final String targetClassName;
    final String targetMethodName;
    final int targetObjectHashCode;
    final Object simpleKey;
    public TemporaryGrailsCacheKey(String targetClassName, String targetMethodName,
                                   int targetObjectHashCode, Object simpleKey) {
        this.targetClassName = targetClassName;
        this.targetMethodName = targetMethodName;
        this.targetObjectHashCode = targetObjectHashCode;
        this.simpleKey = simpleKey;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
        + ((simpleKey == null) ? 0 : simpleKey.hashCode());
        result = prime * result
        + ((targetClassName == null) ? 0 : targetClassName
                .hashCode());
        result = prime * result
        + ((targetMethodName == null) ? 0 : targetMethodName
                .hashCode());
        result = prime * result + targetObjectHashCode;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this.is(obj))
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TemporaryGrailsCacheKey other = (TemporaryGrailsCacheKey) obj;
        if (simpleKey == null) {
            if (other.simpleKey != null)
                return false;
        } else if (!simpleKey.equals(other.simpleKey))
            return false;
        if (targetClassName == null) {
            if (other.targetClassName != null)
                return false;
        } else if (!targetClassName.equals(other.targetClassName))
            return false;
        if (targetMethodName == null) {
            if (other.targetMethodName != null)
                return false;
        } else if (!targetMethodName.equals(other.targetMethodName))
            return false;
        if (targetObjectHashCode != other.targetObjectHashCode)
            return false;
        return true;
    }
}
