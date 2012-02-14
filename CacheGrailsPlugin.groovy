class CacheGrailsPlugin {
    def version = "0.5.BUILD-SNAPSHOT"
    def grailsVersion = "2.0 > *"

    def title = "Cache Plugin"
    def author = "Jeff Brown"
    def authorEmail = "jbrown@vmware.com"
    def description = 'Grails Cache Plugin'
    def documentation = "http://grails.org/plugin/cache"

    def license = "APACHE"
    def organization = [ name: "SpringSource", url: "http://www.springsource.org/" ]
    def developers = [ [ name: "Burt Beckwith", email: "beckwithb@vmware.com" ]]
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPCACHE" ]
    def scm = [ url: "https://github.com/grails-plugins/grails-cache" ]

    def pluginExcludes = [
        '**/com/demo/**',
        'grails-app/i18n/**',
        'web-app/**'
    ]

    def doWithSpring = {
        xmlns cache: 'http://www.springframework.org/schema/cache'
        cache.'annotation-driven'()
        
        
        // obviously this is temporary...
        
        def configuredCaches = []
        def cacheConfig = application.config.grails?.cache
        if(cacheConfig) {
            def configBuilder = new org.grails.plugin.cache.ConfigBuilder()
            def configs = configBuilder.evaluate(cacheConfig)
            if(configs) {
                for(config in configs) {
                    def cacheName = config.name
                    def cacheType = config.type
                    "${cacheName}"(cacheType)
                    configuredCaches << ref(cacheName)
                }
                cacheManager(org.springframework.cache.support.SimpleCacheManager) {
                   caches = configuredCaches
                }
            }
        }               
    }
}
