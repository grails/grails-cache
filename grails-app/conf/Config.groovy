grails.doc.authors = 'Jeff Brown, Burt Beckwith'
grails.doc.license = 'Apache License 2.0'
grails.doc.title = 'Cache Plugin'
//grails.doc.subtitle = ''
//grails.doc.copyright = ''
//grails.doc.footer = ''

log4j = {
	error 'org.codehaus.groovy.grails',
			'org.springframework',
			'org.hibernate',
			'net.sf.ehcache.hibernate'
	debug 'grails.plugin.cache'
}

// for tests
grails.cache.config = {
	cache {
		name 'fromConfigGroovy'
	}
}
