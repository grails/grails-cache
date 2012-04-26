package org.grails.plugin.cache

class CacheTagLib {

    static namespace = 'cache'

    def grailsCacheManager

    /**
     * Renders a block of markup and caches the result so the next time the same block
     * is rendered, it does not need to be evaluated again.
     * 
     * @attr key An optional cache key allowing the same block to be cached with different content
     */
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

    /**
     * Renders a GSP template and caches the result so the next time the same template 
     * is rendered, it does not need to be evaluated again.
     * 
     * @attr template REQUIRED The name of the template to apply
     * @attr key An optional cache key allowing the same template to be cached with different content
     * @attr contextPath the context path to use (relative to the application context path). Defaults to "" or path to the plugin for a plugin view or template.
     * @attr bean The bean to apply the template against
     * @attr model The model to apply the template against as a java.util.Map
     * @attr collection A collection of model objects to apply the template to
     * @attr var The variable name of the bean to be referenced in the template
     * @attr plugin The plugin to look for the template in
     */
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
