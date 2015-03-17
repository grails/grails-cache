grails.doc.authors = 'Jeff Brown, Burt Beckwith'
grails.doc.license = 'Apache License 2.0'
grails.doc.title = 'Cache Plugin'

// for tests
grails.cache.config = {
	cache {
		name 'fromConfigGroovy1'
	}
	cache {
		name 'fromConfigGroovy2'
	}
}

dataSource {
    pooled = true
    driverClassName = 'org.h2.Driver'
    username = 'sa'
    password = ''
    dbCreate = 'update'
    url = 'jdbc:h2:mem:testDb'
}

hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
}

spring.groovy.template.'check-template-location' = false
