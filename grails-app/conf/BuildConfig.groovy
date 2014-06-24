import grails.util.Environment

if(System.getenv('TRAVIS_BRANCH')) {
    grails.project.repos.grailsCentral.username = System.getenv("GRAILS_CENTRAL_USERNAME")
    grails.project.repos.grailsCentral.password = System.getenv("GRAILS_CENTRAL_PASSWORD")
}

grails.project.fork = [
   test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true], // configure settings for the test-app JVM
   run: [maxMemory: 1024, minMemory: 256, debug: false, maxPerm: 256], // configure settings for the run-app JVM
   war: [maxMemory: 1024, minMemory: 256, debug: false, maxPerm: 256], // configure settings for the run-war JVM
   console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]// configure settings for the Console UI JVM
]

grails.project.work.dir = 'target'
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

    compile "org.javassist:javassist:3.17.1-GA"

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

		build(':release:3.0.1', ':rest-client-builder:1.0.3') {
				export = false
		}

    compile ':scaffolding:2.0.1', { export = false }

    test(':functional-test:2.0.0') {
			export = false
		}

    compile ':webxml:1.4.1'
	}
}
