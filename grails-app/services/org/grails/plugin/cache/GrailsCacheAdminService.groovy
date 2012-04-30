package org.grails.plugin.cache

import grails.plugin.cache.CacheEvict

class GrailsCacheAdminService {

    static transactional = false

    @CacheEvict(value="grailsBlocksCache", allEntries=true)
    def clearBlocksCache() {}
    
    @CacheEvict(value="grailsTemplatesCache", allEntries=true)
    def clearTemplatesCache() {}
}

