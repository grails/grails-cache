package com.demo

import grails.plugin.cache.GrailsConcurrentLinkedMapCache
import grails.plugin.cache.GrailsConcurrentLinkedMapCacheManager
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.lang.Unroll

/**
 * This test case validates the documented cache configuration behavior and fix for
 * issue https://github.com/grails-plugins/grails-cache/issues/62
 */
@Integration
class MaxCapacityCacheSpec extends Specification {

    GrailsConcurrentLinkedMapCacheManager grailsCacheManager

    @Unroll
    void "Verify max capacities set for configured caches"() {
        when:
            GrailsConcurrentLinkedMapCache cache = grailsCacheManager.getCache(cacheName) as GrailsConcurrentLinkedMapCache

        then:
            cache.capacity == expectedCapacity

        where:
            cacheName | expectedCapacity
            'foo'     | 100
            'bar'     | 200

    }
}