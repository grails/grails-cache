package com.demo

import grails.plugin.cache.Cacheable

class DemoController {

	def basicCachingService
	def grailsCacheAdminService

	@Cacheable('show')
	def show (params) {
		[param: params.id]
	}
	
	def clearBlocksCache() {
		grailsCacheAdminService.clearBlocksCache()
		render "cleared blocks cache"
	}

	def clearTemplatesCache() {
		grailsCacheAdminService.clearTemplatesCache()
		render "cleared templates cache"
	}

	def basicCachingServiceInvocationCount() {
		render "Basic Caching Service Invocation Count Is ${basicCachingService.invocationCounter}."
	}

	def basicCachingService() {
		render "Value From Service Is \"${basicCachingService.data}\""
	}

	def basicCachingServiceInvocation2Count() {
		render "Basic Caching Service Invocation Count Is ${basicCachingService.invocationCounter2}."
	}

	def basicCaching2Service() {
		render "Value From Service Is \"${basicCachingService.data2}\""
	}

	def basicCachingWithParamService() {
		render "Value From Service Is \"${basicCachingService.getDataWithParams("dummy")}\""
	}

	def basicCaching2WithParamService() {
		render "Value From Service Is \"${basicCachingService.getData2WithParams("dummy")}\""
	}

	def basicResetCachingService() {
		render "Value From Service Is \"${basicCachingService.resetData()}\""
	}

	def cachePut(String key, String value) {
		def result = basicCachingService.getData(key, value)
		render "Result: ${result}"
	}

	def cacheGet(String key) {
		def result = basicCachingService.getData(key)
		render "Result: ${result}"
	}

	def cacheEvictAndGet(String key) {
		basicCachingService.getDataEvict(key)
		def result = basicCachingService.getData(key)
		render "Result: ${result}"
	}

	def cacheEvictAllAndGet(String key) {
		basicCachingService.getDataEvictAll()
		def result = basicCachingService.getData(key)
		render "Result: ${result}"
	}

	def cacheClearAndGet(String key) {
		grailsCacheAdminService.clearCache('basic')
		def result = basicCachingService.getData(key)
		render "Result: ${result}"
	}

	def blockCache(int counter) {
		[counter: counter]
	}

	def renderTag(int counter) {
		[counter: counter]
	}

	def blockCacheTTL(int counter, int ttl) {
		[counter: counter, ttl: ttl]
	}

	def renderTagTTL(int counter, int ttl) {
		[counter: counter, ttl: ttl]
	}
}
