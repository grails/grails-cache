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
    
    void testBasicCachePutService() {
    	get '/demo/cacheGet?key=band'
    	assertStatus 200
    	assertContentContains 'Result: null'
    	
    	get '/demo/cachePut?key=band&value=Thin+Lizzy'
    	assertStatus 200
    	assertContentContains 'Result: ** Thin Lizzy **'
		
    	get '/demo/cacheGet?key=band'
    	assertStatus 200
    	assertContentContains 'Result: ** Thin Lizzy'

		get '/demo/cacheGet?key=singer'
		assertStatus 200
		assertContentContains 'Result: null'
		
		get '/demo/cachePut?key=singer&value=Phil+Lynott'
		assertStatus 200
		assertContentContains 'Result: ** Phil Lynott **'
		
		get '/demo/cacheGet?key=singer'
		assertStatus 200
		assertContentContains 'Result: ** Phil Lynott'

		get '/demo/cachePut?key=singer&value=John+Sykes'
		assertStatus 200
		assertContentContains 'Result: ** John Sykes **'
		
		get '/demo/cacheGet?key=singer'
		assertStatus 200
		assertContentContains 'Result: ** John Sykes'
    }
}