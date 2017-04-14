package grails.plugin.cache

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(value = 'grails.cache')
class CachePluginConfiguration {

    Boolean clearAtStartup = false
    Map<String, CacheConfig> caches = [:]

    class CacheConfig {
        Integer maxCapacity
    }
}

