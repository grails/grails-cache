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

import groovy.transform.CompileStatic
import org.grails.plugin.cache.GrailsCacheManager
import org.springframework.cache.Cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Based on com.google.guava.Cache
 *
 * @author Moritz Kobel
 * based on Jakob Drangmeister's GrailsConcurrentLinkedMapCacheManager
 */
@CompileStatic
class GrailsGuavaCacheManager implements GrailsCacheManager {

   protected final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>()
   final static int DEFAULT_TTL = 3600

   Collection<String> getCacheNames() {
      return Collections.unmodifiableSet(cacheMap.keySet())
   }

   Cache getCache(String name) {
      return getCache(name, 10000, DEFAULT_TTL)
   }

   Cache getCache(String name, int capacity, Integer ttl) {
      Cache cache = cacheMap.get(name)
      if (cache == null) {
         cache = createGuavaCache(name, capacity, ttl ?: DEFAULT_TTL)
         Cache existing = cacheMap.putIfAbsent(name, cache)
         if (existing != null) {
            cache = existing
         }
      }
      return cache
   }

   boolean cacheExists(String name) {
      getCacheNames().contains(name)
   }

   boolean destroyCache(String name) {
      cacheMap.remove(name) != null
   }

   protected GrailsGuavaCache createGuavaCache(String name, int capacity, int ttl) {
      return new GrailsGuavaCache(name, capacity, ttl)
   }

   void setConfiguration(CachePluginConfiguration configuration) {
      configuration.caches.each { String key, CachePluginConfiguration.CacheConfig value ->
         getCache(key, value.maxCapacity, value.ttl ?: configuration.defaultTtl)
      }
   }
}
