/* Copyright 2013 SpringSource.
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

import com.google.common.cache.Cache
import org.junit.Test
import org.springframework.cache.support.SimpleValueWrapper

/**
 * @author Moritz Kobel
 * based on Jakob Drangmeister's GrailsConcurrentLinkedMapCacheTests
 */
class GrailsGuavaMapCacheTests {

    @Test
   void testCreateCache() {
      GrailsGuavaCache smallCache = new GrailsGuavaCache("smallCache", 1000, 60)

      assert smallCache.getName() == "smallCache"
      assert smallCache.getNativeCache() instanceof Cache
      assert smallCache.getCapacity() == 1000
      assert smallCache.isAllowNullValues() == true

      GrailsGuavaCache bigCache = new GrailsGuavaCache("bigCache", 5000000, 60,false)

      assert bigCache.getName() == "bigCache"
      assert bigCache.getNativeCache() instanceof Cache
      assert bigCache.getCapacity() == 5000000
      assert bigCache.isAllowNullValues() == false
   }

   @Test
   void testPutAndGet() {
      GrailsGuavaCache cache = new GrailsGuavaCache("cache", 1000, 60,true)

      cache.put("key", "value");

      assert cache.getSize() == 1
      GrailsValueWrapper value = cache.get("key")
      assert value.get().equals("value")
   }

   @Test
   void testPutIfAbsent() {
      GrailsGuavaCache cache = new GrailsGuavaCache("cache", 1000, 60,true)
      cache.put("key", "value")
      cache.putIfAbsent("key", "value") instanceof SimpleValueWrapper
      assert cache.getSize() == 1
   }

   @Test
   void testEvict() {
      GrailsGuavaCache cache = new GrailsGuavaCache("cache", 10, 60,true)
      cache.put("key", "value");
      assert cache.getSize() == 1

      cache.evict("key")
      assert cache.getSize() == 0

   }

   @Test
   void testCacheCapacity() {
      GrailsGuavaCache cache = new GrailsGuavaCache("cache", 1000, 60, true)
      assert cache.getCapacity() == 1000

      for(int i = 0; i < 2000; i++) {
         cache.put(i, i)
      }

      assert cache.getSize() == 1000
   }

   @Test
   void testCacheTtl() {
      GrailsGuavaCache cache = new GrailsGuavaCache("cache", 10, 20, true)
      assert cache.getCapacity() == 10

      for(int i = 0; i < 10; i++) {
         cache.put(i, i)
      }

      assert cache.getSize() == 10

      Thread.sleep(20000l)

      cache.put("one","one") // write triggers eviction

      assert cache.getSize() == 1

   }


   @Test
   void testClear() {
      GrailsGuavaCache cache = new GrailsGuavaCache("cache", 1000, 60, true)
      assert cache.getCapacity() == 1000

      cache.put("key", "value")
      assert cache.getSize() == 1

      cache.clear()

      assert cache.getSize() == 0
   }

}
