package com.demo

import grails.plugin.cache.CustomCacheKeyGenerator
import grails.plugin.cache.GrailsConcurrentMapCacheManager
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(BasicCachingService)
class BasicCachingServiceSpec extends Specification {

    static doWithSpring = {
        grailsCacheManager GrailsConcurrentMapCacheManager
        customCacheKeyGenerator CustomCacheKeyGenerator
    }

    void 'test invoking cacheable method when cache manager is present'() {
        when: 'a cached method is invoked the first time'
        def result = service.getData()

        then: 'the code in the method is exeucted'
        result == 'Hello World!'
        service.invocationCounter == 1

        when: 'a cached method is invoked the second time'
        result = service.getData()

        then: 'the cached return value is returned and the code in the method is not executed'
        result == 'Hello World!'
        service.invocationCounter == 1
    }
}
