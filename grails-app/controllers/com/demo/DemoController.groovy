package com.demo

class DemoController {

    def basicCachingService

    def basicCachingServiceInvocationCount() {
        render "Basic Caching Service Invocation Count Is ${basicCachingService.invocationCounter}."
    }

    def basicCachingService() {
        render "Value From Service Is \"${basicCachingService.data}\""
    }
    
    def cacheTagBasics(int counter) {
        [counter: counter]
    }
    
    def mapAsKey(int counter) {
        [counter: counter]
    }
    
    def renderTag() {
        [:]
    }
}
