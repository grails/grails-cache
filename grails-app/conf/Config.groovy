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
		name 'fromConfigGroovy1'
	}
	cache {
		name 'fromConfigGroovy2'
	}
}

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
