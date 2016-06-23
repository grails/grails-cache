package grails.plugin.cache

interface GrailsCacheKeyGenerator {
    def generate(String className, String methodName, int objHashCode, Map methodParams, String spel)
}
