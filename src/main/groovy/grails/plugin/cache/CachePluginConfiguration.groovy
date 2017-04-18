package grails.plugin.cache

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for the cache plugin
 *
 * @author Graeme Rocher
 * @author James Kleeh
 */
@CompileStatic
@ConfigurationProperties(value = 'grails.cache')
class CachePluginConfiguration {

    Boolean clearAtStartup = false
    Map<String, CacheConfig> caches = [:]

    class CacheConfig {
        Integer maxCapacity
    }
}

