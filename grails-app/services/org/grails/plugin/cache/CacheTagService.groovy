package org.grails.plugin.cache

import org.springframework.cache.annotation.Cacheable

class CacheTagService {

    static transactional = false

    def groovyPagesTemplateRenderer

    @Cacheable(value='grailsBlocksCache', key='#key')
    def getContent(key, body) {
        body()
    }

    @Cacheable(value="grailsTemplatesCache", key="#key")
    def getRenderedTemplate(key, webRequest, pageScope, attrs) {
        def body = null
        def out = new StringWriter()
        groovyPagesTemplateRenderer.render(webRequest, pageScope, attrs, body, out)
        out
    }
}
