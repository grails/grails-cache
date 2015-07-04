package com.demo

import grails.test.mixin.integration.*
import geb.spock.GebSpec

@Integration
class CachingServiceIntegrationSpec extends GebSpec {

	void 'test caching service with method params'() {
		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 100.'

		when:
		// first retrieval. no cache. service method is invoked for each call.
		go '/demo/basicCachingService'

		then:
		$().text().contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		go '/demo/basicCaching2Service'

		then:
		$().text().contains 'Value From Service Is "Hello World 2!"'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 101.'

		when:
		// there's should be no change because it took the value from cache. and both service methods don't get mixed up
		go '/demo/basicCachingService'

		then:
		$().text().contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		go '/demo/basicCaching2Service'

		then:
		$().text().contains 'Value From Service Is "Hello World 2!"'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 101.'

		when:
		go '/demo/basicCaching2Service'

		then:
		$().text().contains 'Value From Service Is "Hello World 2!"'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 101.'

		when:
		go '/demo/basicCachingService'

		then:
		$().text().contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		// reset
		go '/demo/basicResetCachingService'
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 100.'
	}

	void 'test basic caching service'() {
		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingService'

		then:
		$().text().contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		go '/demo/basicCachingService'

		then:
		$().text().contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		$().text().contains 'Basic Caching Service Invocation Count Is 1.'
	}

	void 'test basic cache put service'() {
		when:
		go '/demo/cacheGet?key=band'

		then:
		$().text().contains 'Result: null'

		when:
		go '/demo/cachePut?key=band&value=Thin+Lizzy'

		then:
		$().text().contains 'Result: ** Thin Lizzy **'

		when:
		go '/demo/cacheGet?key=band'

		then:
		$().text().contains 'Result: ** Thin Lizzy'

		when:
		go '/demo/cacheGet?key=singer'

		then:
		$().text().contains 'Result: null'

		when:
		go '/demo/cachePut?key=singer&value=Phil+Lynott'

		then:
		$().text().contains 'Result: ** Phil Lynott **'

		when:
		go '/demo/cacheGet?key=singer'

		then:
		$().text().contains 'Result: ** Phil Lynott'

		when:
		go '/demo/cachePut?key=singer&value=John+Sykes'

		then:
		$().text().contains 'Result: ** John Sykes **'

		when:
		go '/demo/cacheGet?key=singer'

		then:
		$().text().contains 'Result: ** John Sykes'

		when:
		go '/demo/cacheGet?key=band'

		then:
		$().text().contains 'Result: ** Thin Lizzy'
	}
}
