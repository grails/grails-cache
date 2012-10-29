import grails.util.Environment

grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for gh-pages branch
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}

	plugins {
		build(":tomcat:$grailsVersion") {
			export = false
		}
		runtime(":hibernate:$grailsVersion") {
		    export = false
		}
		if (Environment.current != Environment.TEST) {
			build(':release:2.0.4', ':rest-client-builder:1.0.2') {
				export = false
			}
		}
		test(':functional-test:1.2.7', ':spock:0.6') {
			export = false
		}
		compile ':webxml:1.4.1'
	}
}
