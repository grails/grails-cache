package org.grails.plugin.cache

class ConfigBuilder {
    
    def configs = []
    
    def evaluate(Closure callable) {
        callable.delegate = this
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable()
        configs
    }
    
    def methodMissing(String methodName, args) {
        configs << [name: methodName, type: org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean]
    }
}