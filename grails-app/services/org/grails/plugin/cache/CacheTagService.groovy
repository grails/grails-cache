package org.grails.plugin.cache

import org.springframework.cache.annotation.Cacheable

class CacheTagService {

    static transactional = false
    static scope = 'singleton'
    def grailsApplication
    
    @Cacheable(value='grailsBlocksCache', key='#key')
    def getContent(key, body) {
        body()
    }
    
    @Cacheable(value="grailsTemplatesCache", key="#key")
    def getRenderedTemplate(key, template, model) {
        def renderArgs = [template: template]
        if(model != null) {
            renderArgs.model = model
        }
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        g.render(renderArgs)
    }
}
