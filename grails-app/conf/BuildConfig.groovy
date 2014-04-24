import grails.util.Environment

grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for gh-pages branch
grails.project.source.level = 1.6

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
        mavenLocal()
        grailsCentral()
        mavenRepo "http://repo.grails.org/grails/core"
	}

	dependencies {
        compile 'cglib:cglib-nodep:2.2.2'
		test 'org.codehaus.gpars:gpars:1.0.0', {
			export = false
		}
		test 'org.codehaus.jsr166-mirror:jsr166y:1.7.0', {
			export = false
		}
        test 'net.sourceforge.nekohtml:nekohtml:1.9.18', { export = false }
        test 'net.sourceforge.htmlunit:htmlunit:2.12', { export = false }
        test 'net.sourceforge.htmlunit:htmlunit-core-js:2.12', { export = false }
	}

	plugins {
		build(":tomcat:7.0.52.1") {
			export = false
		}
		
        runtime(":hibernate:3.6.10.13") {
			export = false
		}
		
        if (Environment.current != Environment.TEST) {
			build(':release:3.0.1', ':rest-client-builder:1.0.3') {
				export = false
			}
		}

        compile ':scaffolding:2.0.1', { export = false }
		
        test(':functional-test:2.0.RC2-SNAPSHOT') {
			export = false
		}
		
        compile ':webxml:1.4.1'
	}
}
