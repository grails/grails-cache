package com.demo

import grails.plugin.cache.CacheEvict
import grails.plugin.cache.CachePut
import grails.plugin.cache.Cacheable
import groovy.transform.CompileStatic

// tag::get_data[]
@CompileStatic
class BasicCachingService {

    private int invocationCounter = 0
    private int conditionalInvocationCounter = 0

    def getInvocationCounter() {
        invocationCounter
    }

    // end::get_data[]

    private int invocationCounter2 = 100

    // tag::get_data[]
    @Cacheable('basic')
    def getData() {
        ++invocationCounter
        'Hello World!'
    }
    // end::get_data[]

    @CacheEvict(value = "basic", allEntries = true)
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

    @Cacheable(value = 'basic', key = { key } )
    def getData(String key) {
    }

    @CachePut(value = 'basic', key = { key } )
    def getData(String key, String value) {
        "** ${value} **"
    }

    @Cacheable(value = 'basic', condition = { x < 10 })
    def multiply(int x, int y) {
        conditionalInvocationCounter++
        x * y
    }

    // tag::get_data[]
}
// end::get_data[]
