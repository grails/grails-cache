package grails.plugin.cache

import org.springframework.cache.CacheManager
import spock.lang.Specification

/**
 * Created by graemerocher on 18/04/2017.
 */
class CacheableParseSpec extends Specification {

    void "test declare condition in closure"() {
        given:
        Class testService = new GroovyShell().evaluate('''
import grails.plugin.cache.*
import groovy.transform.CompileStatic

@CompileStatic
class TestService {
    @Cacheable(value = 'basic', condition = { x < 10 })
    def multiply(int x, int y) {
        x * y
    }
}
return TestService

''')
        CacheManager cm = Mock(CacheManager)
        def instance = testService.newInstance()
//        instance.setGrailsCacheManager(cm)
        expect:
        instance.multiply(1,2) ==2
    }
}
