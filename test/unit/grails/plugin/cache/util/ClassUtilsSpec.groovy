package grails.plugin.cache.util

/**
 * @author Jeff Brown
 */
class ClassUtilsSpec {
}

class SomeGroovyClass {

    public publicField = 2
    private int privateField = 3
    private propertyWithPrivateField = 4
    def getSomeProperty() {
        1
    }

    def getPropertyWithPrivateField() {
        100
    }
}
