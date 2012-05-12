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

/**
 * @author Burt Beckwith
 */
class ConfigLoaderTests extends GroovyTestCase {

	def grailsApplication
	def grailsCacheConfigLoader
	def grailsCacheManager

	void testConfigClasses() {
		def configClasses = grailsApplication.cacheConfigClasses
		assertEquals 2, configClasses.length
		assertTrue configClasses.clazz.name.contains('DefaultCacheConfig')
		assertTrue configClasses.clazz.name.contains('TestCacheConfig')
	}

	void testLoadConfigs() {

		grailsCacheConfigLoader.reload grailsApplication.mainContext

		assertEquals(['basic', 'fromConfigGroovy1', 'fromConfigGroovy2',
		              'grailsBlocksCache', 'grailsTemplatesCache'],
		             grailsCacheManager.cacheNames.sort())

		// simulate editing Config.groovy
		grailsApplication.config.grails.cache.config = {
			cache {
				name 'fromConfigGroovy1'
			}
			cache {
				name 'fromConfigGroovy_new'
			}
		}

		grailsCacheConfigLoader.reload grailsApplication.mainContext

		assertEquals(['basic', 'fromConfigGroovy1', 'fromConfigGroovy_new',
		              'grailsBlocksCache', 'grailsTemplatesCache'],
		             grailsCacheManager.cacheNames.sort())
	}

	void testOrder() {

		grailsCacheConfigLoader.reload grailsApplication.mainContext

		assertEquals(['basic', 'fromConfigGroovy1', 'fromConfigGroovy2',
		              'grailsBlocksCache', 'grailsTemplatesCache'],
		             grailsCacheManager.cacheNames.sort())

		// simulate editing Config.groovy
		grailsApplication.config.grails.cache.config = {
			cache {
				name 'fromConfigGroovy1'
			}
			cache {
				name 'fromConfigGroovy_new2'
			}
		}

		grailsCacheConfigLoader.reload grailsApplication.mainContext

		assertEquals(['basic', 'fromConfigGroovy1', 'fromConfigGroovy_new2',
		              'grailsBlocksCache', 'grailsTemplatesCache'],
		             grailsCacheManager.cacheNames.sort())
	}

	protected void setUp() {
		super.setUp()
		reset()
	}

	protected void tearDown() {
		super.tearDown()
		reset()
	}

	private void clearCaches() {
		for (String name in grailsCacheManager.cacheNames) {
			assertTrue grailsCacheManager.destroyCache(name)
		}
	}

	private void reset() {

		clearCaches()

		grailsApplication.config.grails.cache.config = {
			cache {
				name 'fromConfigGroovy1'
			}
			cache {
				name 'fromConfigGroovy2'
			}
		}
	}
}
