package org.grails.plugin.cache

import org.springframework.cache.annotation.Cacheable

class CacheTagService {

    static transactional = false
    static scope = 'singleton'
    
    @Cacheable(value='blocks', key='#key')
    def getContent(key, body) {
        body()
    }
}
