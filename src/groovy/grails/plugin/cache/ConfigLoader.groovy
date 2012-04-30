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
package grails.plugin.cache

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

import grails.plugin.cache.CacheConfigArtefactHandler.CacheConfigGrailsClass
import grails.util.Environment

/**
 * @author Burt Beckwith
 */
class ConfigLoader {

	static final int DEFAULT_ORDER = 1000

	void reload(ApplicationContext ctx) {
		def application = ctx.grailsApplication
		List<ConfigObject> configs = loadOrderedConfigs(application)
		reload configs, ctx
	}

	void reload(List<ConfigObject> configs, ApplicationContext ctx) {

		// order doesn't matter in this impl, but in general process in reverse order so
		// lower order values have higher priority and can override previous settings
		def configuredCacheNames = [] as LinkedHashSet
		for (ListIterator<ConfigObject> iter = configs.listIterator(configs.size()); iter.hasPrevious(); ) {
			ConfigObject co = iter.previous()
			ConfigBuilder builder = new ConfigBuilder()
			def config = co.cacheConfig ?: co.config
			if (config instanceof Closure) {
				builder.parse config
			}
			configuredCacheNames.addAll builder.cacheNames
		}

		GrailsCacheManager cacheManager = ctx.grailsCacheManager

		for (String name in cacheManager.cacheNames) {
			if (!configuredCacheNames.contains(name)) {
				cacheManager.destroyCache name
			}
		}

		for (String cacheName in configuredCacheNames) {
			cacheManager.getCache cacheName
		}
	}

	List<ConfigObject> loadOrderedConfigs(GrailsApplication application) {
		ConfigSlurper slurper = new ConfigSlurper(Environment.current.name)

		List<ConfigObject> configs = []
		def cacheConfig
		for (configClass in application.cacheConfigClasses) {
		   def config = slurper.parse(configClass.clazz)
		   cacheConfig = config.cacheConfig
		   if ((cacheConfig instanceof Closure) && processConfig(config, configClass)) {
				configs << config
		   }
		}

		cacheConfig = application.config.grails.cache
	   if ((cacheConfig.config instanceof Closure) && processConfig(cacheConfig, null)) {
			configs << cacheConfig
	   }

		sortConfigs configs

		configs
	}

	protected boolean processConfig(ConfigObject config, CacheConfigGrailsClass configClass) {
		def cacheConfig
		String sourceClassName

		if (configClass == null) {
			cacheConfig = config.config
			sourceClassName = 'Config'
		}
		else {
			cacheConfig = config.cacheConfig
			sourceClassName = configClass.clazz.name
		}

		if (cacheConfig instanceof Closure) {
			def order = config.order
			if (!(order instanceof Number)) {
				config.order = DEFAULT_ORDER
			}
			config._sourceClassName = sourceClassName
			return true
		}

		false
	}

	protected void sortConfigs(List<Closure> configs) {
		configs.sort { c1, c2 ->
			c1.order == c2.order ? c1._sourceClassName <=> c2._sourceClassName : c1.order <=> c2.order
		}
	}
}
