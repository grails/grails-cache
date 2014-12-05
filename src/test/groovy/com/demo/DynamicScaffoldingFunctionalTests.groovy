package com.demo

import functionaltestplugin.FunctionalTestCase

class DynamicScaffoldingFunctionalTests extends FunctionalTestCase {

    void testDynamicScaffolding() {
        get '/person/index'
        assertStatus 200
        assertTitle 'Person List'
    }
}
