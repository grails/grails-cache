/* Copyright 2012 SpringSource.
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
import grails.plugin.cache.ConfigBuilder
import grails.plugin.cache.GrailsConcurrentMapCacheManager
import grails.plugin.cache.web.ProxyAwareMixedGrailsControllerHelper
import grails.plugin.cache.web.filter.DefaultWebKeyGenerator
import grails.plugin.cache.web.filter.NoOpFilter
import grails.plugin.cache.web.filter.ExpressionEvaluator
import grails.plugin.cache.web.filter.simple.MemoryPageFragmentCachingFilter

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean
import org.springframework.core.Ordered
import org.springframework.web.filter.DelegatingFilterProxy

class CacheGrailsPlugin {

	private final Logger log = LoggerFactory.getLogger('grails.plugin.cache.CacheGrailsPlugin')

	def version = '0.5.BUILD-SNAPSHOT'
	def grailsVersion = '2.0 > *'
	def observe = ['controllers']
	def loadAfter = ['controllers']

	def title = 'Cache Plugin'
	def author = 'Jeff Brown'
	def authorEmail = 'jbrown@vmware.com'
	def description = 'Grails Cache Plugin'
	def documentation = 'http://grails.org/plugin/cache'

	def license = 'APACHE'
	def organization = [name: 'SpringSource', url: 'http://www.springsource.org/']
	def developers = [[name: 'Burt Beckwith', email: 'beckwithb@vmware.com']]
	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPCACHE']
	def scm = [url: 'https://github.com/grails-plugins/grails-cache']

	def pluginExcludes = [
		'**/com/demo/**',
		'grails-app/i18n/**',
		'grails-app/views/**',
		'web-app/**'
	]

	def getWebXmlFilterOrder() {
		def FilterManager = getClass().getClassLoader().loadClass('grails.plugin.webxml.FilterManager')
		[grailsCacheFilter: FilterManager.URL_MAPPING_POSITION + 1000]
	}

	def doWithWebDescriptor = {xml ->
		if (!isEnabled(application)) {
			return
		}

		def filters = xml.filter
		def lastFilter = filters[filters.size() - 1]
		lastFilter + {
			filter {
				'filter-name'('grailsCacheFilter')
				'filter-class'(DelegatingFilterProxy.name)
				'init-param' {
					'param-name'('targetFilterLifecycle')
					'param-value'('true')
				}
			}
		}

		def filterMappings = xml.'filter-mapping'
		def lastMapping = filterMappings[filterMappings.size() - 1]
		lastMapping + {
			'filter-mapping' {
				'filter-name'('grailsCacheFilter')
				'url-pattern'('*.dispatch')
				'dispatcher'('FORWARD')
				'dispatcher'('INCLUDE')
			}
		}
	}

	def doWithSpring = {

		if (!isEnabled(application)) {
			log.warn 'Cache plugin is disabled'
			grailsCacheFilter(NoOpFilter)
			return
		}

		def cacheConfig = application.config.grails.cache
		String cacheManagerBeanName = cacheConfig.cacheManagerBeanName ?: 'cacheManager'
		def proxyTargetClass = cacheConfig.proxyTargetClass
		if (!(proxyTargetClass instanceof Boolean)) proxyTargetClass = false
		def order = cacheConfig.aopOrder
		if (!(order instanceof Number)) order = Ordered.LOWEST_PRECEDENCE

		xmlns cache: 'http://www.springframework.org/schema/cache'
		cache.'annotation-driven'('cache-manager': cacheManagerBeanName,
		                          mode: 'proxy', order: order,
		                          'proxy-target-class': proxyTargetClass)

		// TODO how do extension plugins configure these?
		def configuredCacheNames = ['grailsBlocksCache', 'grailsTemplatesCache']
		if (cacheConfig) {
			ConfigBuilder builder = new ConfigBuilder()
			builder.parse cacheConfig.config
			configuredCacheNames.addAll builder.cacheNames
		}

		cacheManager(GrailsConcurrentMapCacheManager) {
			// TODO this locks the manager and doesn't allow new caches;
			// could call getCache() for each name in doWithApplicationContext instead
			cacheNames = configuredCacheNames
		}

		webCacheKeyGenerator(DefaultWebKeyGenerator)

		webExpressionEvaluator(ExpressionEvaluator)

		grailsCacheFilter(MemoryPageFragmentCachingFilter) {
			cacheManager = ref('cacheManager')
			// TODO this name might be brittle - perhaps do by type?
			cacheOperationSource = ref('org.springframework.cache.annotation.AnnotationCacheOperationSource#0')
			keyGenerator = ref('webCacheKeyGenerator')
			expressionEvaluator = ref('webExpressionEvaluator')
		}

		grailsControllerHelper(ProxyAwareMixedGrailsControllerHelper) {
			grailsApplication = ref('grailsApplication')
		}
	}

	def onChange = { event ->

		if (!isEnabled(event.application)) {
			return
		}

		if (!event.source) {
			return
		}

		if (event.application.isControllerClass(event.source)) {
			// TODO reload CacheOperation config based on updated annotations
		}

		if (event.application.isServiceClass(event.source)) {
			// TODO reload CacheOperation config based on updated annotations
		}
	}

	private boolean isEnabled(GrailsApplication application) {
		// TODO
		true
	}
}
