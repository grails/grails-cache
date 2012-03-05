package com.demo

class CachingServiceFunctionalTests extends functionaltestplugin.FunctionalTestCase {

    void testBasicCachingService() {
        get '/demo/basicCachingServiceInvocationCount'
        assertStatus 200
        assertContentContains 'Basic Caching Service Invocation Count Is 0.'

        get '/demo/basicCachingServiceInvocationCount'
        assertStatus 200
        assertContentContains 'Basic Caching Service Invocation Count Is 0.'

        get '/demo/basicCachingService'
        assertStatus 200
        assertContentContains 'Value From Service Is "Hello World!"'

        get '/demo/basicCachingServiceInvocationCount'
        assertStatus 200
        assertContentContains 'Basic Caching Service Invocation Count Is 1.'

        get '/demo/basicCachingService'
        assertStatus 200
        assertContentContains 'Value From Service Is "Hello World!"'

        get '/demo/basicCachingServiceInvocationCount'
        assertStatus 200
        assertContentContains 'Basic Caching Service Invocation Count Is 1.'
    }
}