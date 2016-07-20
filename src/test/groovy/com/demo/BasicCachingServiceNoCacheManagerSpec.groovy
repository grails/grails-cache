package com.demo

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(BasicCachingService)
class BasicCachingServiceNoCacheManagerSpec extends Specification {

    void 'test invoking cacheable method when no cache manager is present'() {
        when: 'a cached method is invoked the first time'
        def result = service.getData()

        then: 'the code in the method is exeucted'
        result == 'Hello World!'
        service.invocationCounter == 1

        when: 'a cached method is invoked the second time'
        result = service.getData()

        then: 'the code in the method is still exeucted because no cache manager is present'
        result == 'Hello World!'
        service.invocationCounter == 2
    }
}
