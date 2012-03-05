grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for gh-pages branch

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
		if (Environment.current != Environment.TEST) {
			build(':release:1.0.1', ':svn:1.0.2') {
				export = false
			}
		}
		test(':functional-test:1.2.7') {
			export = false
		}
		compile ':webxml:1.4.1'
	}
}
