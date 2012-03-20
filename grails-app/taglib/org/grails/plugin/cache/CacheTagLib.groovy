package org.grails.plugin.cache

class CacheTagLib {
    static namespace = 'cache'
    def cacheTagService
    
    def block = { attrs, body ->
        out << cacheTagService.getContent(attrs.key, body)
    }
}
