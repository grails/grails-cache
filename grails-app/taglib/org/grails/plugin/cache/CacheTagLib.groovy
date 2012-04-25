package org.grails.plugin.cache

class CacheTagLib {

    static namespace = 'cache'

    def grailsCacheManager

    def block = { attrs, body ->
        def cache = grailsCacheManager.getCache('grailsBlocksCache')
        def bodyClosure = body.@bodyClosure
        def closureClass = bodyClosure.getClass()
        def key = closureClass.getName()
        if(attrs.key) {
            key = key + ':' + attrs.key
        }
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
        // TODO using attrs.template is not adequate here, we need the full path to the template
        def key = attrs.template
        
        if(attrs.key) {
            key = key + ':' + attrs.key
        }
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
