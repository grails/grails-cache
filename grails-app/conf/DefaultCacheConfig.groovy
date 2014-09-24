config = {
   cacheManager = "GrailsConcurrentLinkedMapCacheManager"
	cache {
		name 'grailsBlocksCache'
      maxCapacity = 10000
	}
	cache {
		name 'grailsTemplatesCache'
      maxCapacity = 10000
	}
}