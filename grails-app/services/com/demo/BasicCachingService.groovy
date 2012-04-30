package com.demo

import grails.plugin.cache.CachePut
import grails.plugin.cache.Cacheable

class BasicCachingService {

    static transactional = false

    private int invocationCounter = 0

    def getInvocationCounter() {
        invocationCounter
    }

    @Cacheable('basic')
    def getData() {
        ++invocationCounter
        'Hello World!'
    }
	
	@Cacheable(value='basic', key='#key')
	def getData(String key) {
	}
	
	@CachePut(value='basic', key='#key')
	def getData(String key, String value) {
		"** ${value} **"
	}
}
