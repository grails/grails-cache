package com.demo

import grails.plugin.cache.CustomCacheKeyGenerator
import grails.plugin.cache.GrailsConcurrentMapCacheManager
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(BasicCachingService)
class BasicCachingServiceSpec extends Specification {

    static doWithSpring = {
        // TODO The plugin should provide a convenient
        // mechanism for setting these up...
        grailsCacheManager(GrailsConcurrentMapCacheManager)
        customCacheKeyGenerator(CustomCacheKeyGenerator)
    }

    void 'test invoking cacheable method when cache manager is present'() {
        when:
        def result = service.getData()

        then:
        result == 'Hello World!'
        service.invocationCounter == 1

        when:
        result = service.getData()

        then:
        result == 'Hello World!'
        service.invocationCounter == 1
    }
}
