package com.demo

import grails.test.mixin.integration.*

@Integration
class DynamicScaffoldingFunctionalTests {

    void testDynamicScaffolding() {
        get '/person/index'
        assertStatus 200
        assertTitle 'Person List'
    }
}
