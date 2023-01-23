package com.demo

import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration

@Integration
class NotCachingControllerIntegrationSpec extends GebSpec {

    void 'test action controller with different parameters'() {
        when:
        go '/demo/show/1'

        then:
        $().text().contains 'Hello World!1'

        when:
        go '/demo/show/2'

        then:
        $().text().contains 'Hello World!2'
    }
}
