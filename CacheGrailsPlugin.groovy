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

    def doWithSpring = {
        xmlns cache: 'http://www.springframework.org/schema/cache'
        cache.'annotation-driven'()
        
        // obviously this is temporary...
        
        basicCache(org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean) {
            name = 'basic'
        }
        
        cacheManager(org.springframework.cache.support.SimpleCacheManager) {
            caches = [basicCache]
        }
    }
}
