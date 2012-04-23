package org.grails.plugin.cache

import org.springframework.cache.annotation.CacheEvict


class GrailsCacheAdminService {

    static transactional = false

    @CacheEvict(value="grailsBlocksCache", allEntries=true)
    def clearBlocksCache() {}
}

