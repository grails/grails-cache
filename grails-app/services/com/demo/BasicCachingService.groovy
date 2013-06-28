package com.demo

import grails.plugin.cache.CachePut
import grails.plugin.cache.Cacheable
import grails.plugin.cache.CacheEvict
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BasicCachingService {

    static transactional = false

    private int invocationCounter = 0
    private int invocationCounter2 = 100

    def getInvocationCounter() {
        invocationCounter
    }
    @Cacheable('basic')
    def getData() {
        ++invocationCounter
        'Hello World!'
    }
    @CacheEvict(value="basic", allEntries=true)
    def resetData() {
        invocationCounter = 0
        invocationCounter2 = 100
        'Reset'
    }

    def getInvocationCounter2() {
        invocationCounter2
    }

    @Cacheable('basic')
    def getData2() {
        ++invocationCounter2
        'Hello World 2!'
    }

    @Cacheable('basic')
    def getDataWithParams(String name) {
        ++invocationCounter
        'Hello World!'
    }

    @Cacheable('basic')
    def getData2WithParams(String name) {
        ++invocationCounter2
        'Hello World 2!'
    }


	@Cacheable(value='basic', key='#key')
	def getData(String key) {
	}

	@CachePut(value='basic', key='#key')
	def getData(String key, String value) {
		"** ${value} **"
	}
}
