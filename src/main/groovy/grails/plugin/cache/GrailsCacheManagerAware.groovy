package grails.plugin.cache

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager

/**
 * A trait for classes that are cache aware
 *
 * @since 4.0
 * @author Graeme Rocher
 */
@CompileStatic
trait GrailsCacheManagerAware {

    @Autowired(required = false)
    private GrailsCacheKeyGenerator customCacheKeyGenerator

    @Autowired(required = false)
    private CacheManager grailsCacheManager

    /**
     * @return The Grails cache manager or null if it isn't present
     */
    CacheManager getGrailsCacheManager() {
        return grailsCacheManager
    }

    /**
     * @return The custom key generator, or null if it isn't present
     */
    GrailsCacheKeyGenerator getCustomCacheKeyGenerator() {
        return customCacheKeyGenerator
    }
}