package org.grails.plugin.cache

class CacheTagLib {
    static namespace = 'cache'
    def cacheTagService
    
    def block = { attrs, body ->
        out << cacheTagService.getContent(attrs.key, body)
    }
    
    def render =  { attrs ->
        def key = attrs.key
        def template = attrs.template
        def model = attrs.model
        out << cacheTagService.getRenderedTemplate(key, template, model)
    }
}
