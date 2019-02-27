package com.demo

import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import spock.lang.Requires

@Integration
class CachingServiceIntegrationSpec extends GebSpec {

	@Requires({sys['geb.env']})
	void 'test caching service with method params'() {
		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 100.'

		when:
		// first retrieval. no cache. service method is invoked for each call.
		go '/demo/basicCachingService'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		go '/demo/basicCaching2Service'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World 2!"'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 101.'

		when:
		// there's should be no change because it took the value from cache. and both service methods don't get mixed up
		go '/demo/basicCachingService'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		go '/demo/basicCaching2Service'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World 2!"'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 101.'

		when:
		go '/demo/basicCaching2Service'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World 2!"'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 101.'

		when:
		go '/demo/basicCachingService'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		// reset
		go '/demo/basicResetCachingService'
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingServiceInvocation2Count'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 100.'
	}

	@Requires({sys['geb.env']})
	void 'test basic caching service'() {
		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 0.'

		when:
		go '/demo/basicCachingService'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 1.'

		when:
		go '/demo/basicCachingService'

		then:
		browser.driver.pageSource.contains 'Value From Service Is "Hello World!"'

		when:
		go '/demo/basicCachingServiceInvocationCount'

		then:
		browser.driver.pageSource.contains 'Basic Caching Service Invocation Count Is 1.'
	}

	@Requires({sys['geb.env']})
	void 'test basic cache put service'() {
		when:
		go '/demo/cacheGet?key=band'

		then:
		browser.driver.pageSource.contains 'Result: null'

		when:
		go '/demo/cachePut?key=band&value=Thin+Lizzy'

		then:
		browser.driver.pageSource.contains 'Result: ** Thin Lizzy **'

		when:
		go '/demo/cacheGet?key=band'

		then:
		browser.driver.pageSource.contains 'Result: ** Thin Lizzy'

		when:
		go '/demo/cacheGet?key=singer'

		then:
		browser.driver.pageSource.contains 'Result: null'

		when:
		go '/demo/cachePut?key=singer&value=Phil+Lynott'

		then:
		browser.driver.pageSource.contains 'Result: ** Phil Lynott **'

		when:
		go '/demo/cacheGet?key=singer'

		then:
		browser.driver.pageSource.contains 'Result: ** Phil Lynott'

		when:
		go '/demo/cachePut?key=singer&value=John+Sykes'

		then:
		browser.driver.pageSource.contains 'Result: ** John Sykes **'

		when:
		go '/demo/cacheGet?key=singer'

		then:
		browser.driver.pageSource.contains 'Result: ** John Sykes'

		when:
		go '/demo/cacheGet?key=band'

		then:
		browser.driver.pageSource.contains 'Result: ** Thin Lizzy'
	}

	@Requires({sys['geb.env']})
	void 'test basic cache evict key service'() {
		when:
		go '/demo/cachePut?key=band&value=Thin+Lizzy'

		then:
		browser.driver.pageSource.contains 'Result: ** Thin Lizzy **'

		when:
		go '/demo/cacheEvictAndGet?key=band'

		then:
		browser.driver.pageSource.contains 'Result: null'
	}

	@Requires({sys['geb.env']})
	void 'test basic cache evict all service'() {
        when:
        go '/demo/cachePut?key=band&value=Thin+Lizzy'

        then:
        browser.driver.pageSource.contains 'Result: ** Thin Lizzy **'

        when:
        go '/demo/cacheEvictAllAndGet?key=band'

        then:
        browser.driver.pageSource.contains 'Result: null'
    }

    void 'test basic cache clear service'() {
        when:
        go '/demo/cachePut?key=band&value=Thin+Lizzy'

        then:
        browser.driver.pageSource.contains 'Result: ** Thin Lizzy **'

        when:
        go '/demo/cacheClearAndGet?key=band'

        then:
        browser.driver.pageSource.contains 'Result: null'
    }
}
