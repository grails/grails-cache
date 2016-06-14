package com.demo

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(BasicCachingService)
class BasicCachingServiceSpec extends Specification {

    void 'test invoking cacheable method when no cache manager is present'() {
        when:
        def result = service.getData()

        then:
        result == 'Hello World!'
        service.invocationCounter == 1

        when:
        result = service.getData()

        then:
        result == 'Hello World!'
        service.invocationCounter == 2
    }
}
