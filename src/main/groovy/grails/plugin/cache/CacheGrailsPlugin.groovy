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

import grails.core.GrailsApplication
import grails.plugin.cache.web.filter.DefaultWebKeyGenerator
import grails.plugin.cache.web.filter.ExpressionEvaluator
import grails.plugin.cache.web.filter.NoOpFilter
import grails.plugin.cache.web.filter.simple.MemoryPageFragmentCachingFilter
import grails.plugins.Plugin
import groovy.util.logging.Commons
import javassist.util.proxy.ProxyFactory
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.cache.Cache
import org.springframework.core.Ordered

import javax.servlet.DispatcherType

@Commons
class CacheGrailsPlugin extends Plugin {

    static {
        ProxyFactory.classLoaderProvider = new ProxyFactory.ClassLoaderProvider() {
            public ClassLoader get(ProxyFactory pf) {
                return Thread.currentThread().getContextClassLoader();
            }
        };
    }

    def grailsVersion = '3.0 > *'
    def observe = ['controllers', 'services']
    def loadAfter = ['controllers', 'services']
    def artefacts = [CacheConfigArtefactHandler]
    def watchedResources = [
            'file:./grails-app/conf/**/*CacheConfig.groovy',
            'file:./plugins/*/grails-app/conf/**/*CacheConfig.groovy'
    ]

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
            'TestCacheConfig**',
            'grails-app/views/**',
            '**/*.gsp'
    ]

    Closure doWithSpring() {
        { ->
            def application = grailsApplication
            if (!isEnabled(application)) {
                log.warn 'Cache plugin is disabled'
                grailsCacheFilter(NoOpFilter)
                return
            }

            def cacheConfig = application.config.grails.cache
            def proxyTargetClass = cacheConfig.proxyTargetClass
            if (!(proxyTargetClass instanceof Boolean)) proxyTargetClass = false
            def order = cacheConfig.aopOrder
            if (!(order instanceof Number)) order = Ordered.LOWEST_PRECEDENCE
            // allow user can to use their own key generator.
            def cacheKeyGen = cacheConfig.keyGenerator ?: 'customCacheKeyGenerator'
            customCacheKeyGenerator(CustomCacheKeyGenerator)

            xmlns cache: 'http://www.springframework.org/schema/cache'

            // creates 3 beans: org.springframework.cache.config.internalCacheAdvisor (org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor),
            //                  org.springframework.cache.annotation.AnnotationCacheOperationSource#0 (org.springframework.cache.annotation.AnnotationCacheOperationSource),
            //                  org.springframework.cache.interceptor.CacheInterceptor#0 (org.springframework.cache.interceptor.CacheInterceptor)

            cache.'annotation-driven'('cache-manager': 'grailsCacheManager', 'key-generator': cacheKeyGen,
                    mode: 'proxy', order: order, 'proxy-target-class': proxyTargetClass)

            // updates the AnnotationCacheOperationSource with a custom subclass
            // and adds the 'cacheOperationSource' alias
            cacheBeanPostProcessor(CacheBeanPostProcessor)

            // Selects cache manager from config
            if (cacheConfig.cacheManager.equals("GrailsConcurrentLinkedMapCacheManager")) {
                grailsCacheManager(GrailsConcurrentLinkedMapCacheManager)
            } else {
                grailsCacheManager(GrailsConcurrentMapCacheManager)
            }


            grailsCacheConfigLoader(ConfigLoader)

            grailsCacheFilter(FilterRegistrationBean) {
                filter = bean(MemoryPageFragmentCachingFilter) {
                    cacheManager = ref('grailsCacheManager')
                    cacheOperationSource = ref('cacheOperationSource')
                    keyGenerator = ref('webCacheKeyGenerator')
                    expressionEvaluator = ref('webExpressionEvaluator')
                }
                urlPatterns = "*"
                dispatcherTypes = EnumSet.of(DispatcherType.FORWARD, DispatcherType.INCLUDE)
            }

            webCacheKeyGenerator(DefaultWebKeyGenerator)

            webExpressionEvaluator(ExpressionEvaluator)
        }
    }

    void doWithApplicationContext() {
        def ctx = applicationContext
        reloadCaches ctx

        def cacheConfig = ctx.grailsApplication.config.grails.cache
        if (cacheConfig.clearAtStartup instanceof Boolean && cacheConfig.clearAtStartup) {
            def grailsCacheManager = ctx.grailsCacheManager
            for (String cacheName in grailsCacheManager.cacheNames) {
                log.info "Clearing cache $cacheName"
                Cache cache = grailsCacheManager.getCache(cacheName)
                cache.clear()
            }
        }
    }

    void onChange(Map<String, Object> event) {

        def application = grailsApplication
        if (!isEnabled(application)) {
            return
        }

        def source = event.source
        if (!source) {
            return
        }

        if (application.isControllerClass(source) || application.isServiceClass(source)) {
            applicationContext.cacheOperationSource.reset()
            log.debug 'Reset GrailsAnnotationCacheOperationSource cache'
        } else if (application.isCacheConfigClass(source)) {
            reloadCaches applicationContext
        }
    }

    void onConfigChange(Map<String, Object> event) {
        reloadCaches applicationContext
    }

    private void reloadCaches(ctx) {

        if (!isEnabled(ctx.grailsApplication)) {
            return
        }

        ctx.grailsCacheConfigLoader.reload ctx
        log.debug 'Reloaded grailsCacheConfigLoader'
    }

    private boolean isEnabled(GrailsApplication application) {
        def enabled = application.config.grails.cache.enabled
        enabled == null || enabled != false
    }
}
