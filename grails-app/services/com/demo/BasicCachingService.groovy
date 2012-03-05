package com.demo

import org.springframework.cache.annotation.Cacheable

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
}
