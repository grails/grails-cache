package com.demo

import functionaltestplugin.FunctionalTestCase

class DynamicScaffoldingFunctionalTests extends FunctionalTestCase {

    void testDynamicScaffolding() {
        get '/person/list'
        assertStatus 200
        assertTitle 'Person List'
    }
}
