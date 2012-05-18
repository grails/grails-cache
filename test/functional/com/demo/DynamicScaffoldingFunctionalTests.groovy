package com.demo

class DynamicScaffoldingFunctionalTests extends functionaltestplugin.FunctionalTestCase {
    
    void testDynamicScaffolding() {
        get '/person/list'
        assertStatus 200
        assertTitle 'Person List'
    }
}