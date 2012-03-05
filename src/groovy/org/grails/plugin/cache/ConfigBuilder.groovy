package org.grails.plugin.cache

import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean

class ConfigBuilder {

    def configs = []

    def evaluate(Closure callable) {
        callable.delegate = this
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable()
        configs
    }

    def methodMissing(String methodName, args) {
        configs << [name: methodName, type: ConcurrentMapCacheFactoryBean]
    }
}