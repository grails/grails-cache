class CacheGrailsPlugin {
    def version = "0.5.BUILD-SNAPSHOT"
    def grailsVersion = "2.0 > *"

    def title = "Cache Plugin"
    def author = "Jeff Brown"
    def authorEmail = "jbrown@vmware.com"
    def description = 'Grails Cache Plugin'
    def documentation = "http://grails.org/plugin/cache"

    def license = "APACHE"
    def organization = [ name: "SpringSource", url: "http://www.springsource.org/" ]
    def developers = [ [ name: "Burt Beckwith", email: "beckwithb@vmware.com" ]]
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPCACHE" ]
    def scm = [ url: "https://github.com/grails-plugins/grails-cache" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
