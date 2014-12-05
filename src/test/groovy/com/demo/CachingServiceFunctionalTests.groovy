package com.demo

import grails.test.mixin.integration.*

@Integration
class CachingServiceFunctionalTests  {

	void testCachingServiceWithSameMethodParams() {
		get '/demo/basicCachingServiceInvocationCount'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 0.'
		get '/demo/basicCachingServiceInvocation2Count'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 100.'

		// first retrieval. no cache. service method is invoked for each call.
		get '/demo/basicCachingService'
		assertStatus 200
		assertContentContains 'Value From Service Is "Hello World!"'
		get '/demo/basicCachingServiceInvocationCount'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 1.'
		get '/demo/basicCaching2Service'
		assertStatus 200
		assertContentContains 'Value From Service Is "Hello World 2!"'
		get '/demo/basicCachingServiceInvocation2Count'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 101.'

		// there's should be no change because it took the value from cache. and both service methods don't get mixed up
		get '/demo/basicCachingService'
		assertStatus 200
		assertContentContains 'Value From Service Is "Hello World!"'
		get '/demo/basicCachingServiceInvocationCount'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 1.'
		get '/demo/basicCaching2Service'
		assertStatus 200
		assertContentContains 'Value From Service Is "Hello World 2!"'
		get '/demo/basicCachingServiceInvocation2Count'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 101.'
		get '/demo/basicCaching2Service'
		assertStatus 200
		assertContentContains 'Value From Service Is "Hello World 2!"'
		get '/demo/basicCachingServiceInvocation2Count'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 101.'
		get '/demo/basicCachingService'
		assertStatus 200
		assertContentContains 'Value From Service Is "Hello World!"'
		get '/demo/basicCachingServiceInvocationCount'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 1.'

		// reset
		get '/demo/basicResetCachingService'
		assertStatus 200
		get '/demo/basicCachingServiceInvocationCount'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 0.'
		get '/demo/basicCachingServiceInvocation2Count'
		assertStatus 200
		assertContentContains 'Basic Caching Service Invocation Count Is 100.'
	}

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

		get '/demo/cacheGet?key=band'
		assertStatus 200
		assertContentContains 'Result: ** Thin Lizzy'
	}
}
