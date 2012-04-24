package org.grails.plugin.cache

class CacheTagLib {

    static namespace = 'cache'

    def grailsCacheManager

    def block = { attrs, body ->
        def cache = grailsCacheManager.getCache('grailsBlocksCache')
        def bodyClosure = body.@bodyClosure
        def closureClass = bodyClosure.getClass()
        def key = closureClass.getName()
        def content = cache.get(key)
        if (content == null) {
            content = body()
            cache.put(key, content)
        } else {
            content = content.get()
        }
        out << content
    }

    def render =  { attrs ->
        def key = attrs.key
        def cache = grailsCacheManager.getCache('grailsTemplatesCache')
        def content = cache.get(key)
        if (content == null) {
            content = g.render(attrs)
            cache.put(key, content)
        } else {
            content = content.get()
        }
        out << content
    }
}
