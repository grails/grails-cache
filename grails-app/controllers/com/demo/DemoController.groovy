package com.demo

class DemoController {

    def basicCachingService

    def basicCachingServiceInvocationCount() {
        render "Basic Caching Service Invocation Count Is ${basicCachingService.invocationCounter}."
    }

    def basicCachingService() {
        render "Value From Service Is \"${basicCachingService.data}\""
    }
}
