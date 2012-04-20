package org.grails.plugin.cache

class CacheTagLib {

    static namespace = 'cache'

    def cacheTagService
    def cacheManager

    def block = { attrs, body ->
        out << cacheTagService.getContent(attrs.key, body)
    }

    def render =  { attrs ->
        def key = attrs.key
        def cache = cacheManager.getCache('grailsTemplatesCache')
        def content = cache.get(key)
        if(content == null) {
            content = g.render(attrs)
            cache.put(key, content)
        } else {
            content = content.get()
        }
        out << content
    }
}
