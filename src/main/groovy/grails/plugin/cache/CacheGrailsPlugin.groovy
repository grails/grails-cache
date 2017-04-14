/* Copyright 2012-2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugin.cache

import grails.plugins.Plugin
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache

@Slf4j
class CacheGrailsPlugin extends Plugin {

    def grailsVersion = "3.2.0 > *"
    def observe = ['controllers', 'services']
    def loadAfter = ['controllers', 'services']
    def title = 'Cache Plugin'
    def author = 'Jeff Brown'
    def authorEmail = 'brownj@ociweb.com'
    def description = 'Grails Cache Plugin'
    def documentation = 'http://grails3-plugins.github.com/cache/latest'
    def profiles = ['web']
    def license = 'APACHE'
    def organization = [name: 'SpringSource', url: 'http://www.springsource.org/']
    def developers = [[name: 'Burt Beckwith', email: 'beckwithb@vmware.com']]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/grails-plugins/grails-cache/issues']
    def scm = [url: 'https://github.com/grails-plugins/grails-cache']


    // resources that should be loaded by the plugin once installed in the application
    //Does this even work anymore?  Doesn't appear to.
    def pluginExcludes = [
            '**/com/demo/**',
            'grails-app/views/**',
            '**/*.gsp'
    ]

    Closure doWithSpring() {
        { ->
            def application = grailsApplication
            if (!application.config.getProperty('grails.cache.enabled', Boolean, true)) {
                log.warn 'Cache plugin is disabled'
                return
            }

            customCacheKeyGenerator(CustomCacheKeyGenerator)

            Class<? extends GrailsCacheManager> cacheClazz = GrailsConcurrentMapCacheManager
            // Selects cache manager from config
            if (application.config.getProperty("grails.cache.cacheManager", String, null) == "GrailsConcurrentLinkedMapCacheManager") {
                cacheClazz = GrailsConcurrentLinkedMapCacheManager
            }

            grailsCacheManager(cacheClazz) {
                configuration = ref('grailsCacheConfiguration')
            }

            grailsCacheConfiguration(CachePluginConfiguration)
        }
    }

    @CompileStatic
    void doWithApplicationContext() {
        CachePluginConfiguration pluginConfiguration = applicationContext.getBean('grailsCacheConfiguration')
        GrailsCacheManager grailsCacheManager = applicationContext.getBean('grailsCacheManager', GrailsCacheManager)

        if (pluginConfiguration.clearAtStartup) {
            for (String cacheName in grailsCacheManager.cacheNames) {
                log.info "Clearing cache $cacheName"
                Cache cache = grailsCacheManager.getCache(cacheName)
                cache.clear()
            }
        }

        List<String> defaultCaches = ['grailsBlocksCache', 'grailsTemplatesCache']
        defaultCaches.each {
            if (!grailsCacheManager.cacheExists(it)) {
                grailsCacheManager.getCache(it)
            }
        }
    }
}
