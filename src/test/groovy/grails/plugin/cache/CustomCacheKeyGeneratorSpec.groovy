package grails.plugin.cache

import spock.lang.Specification
import spock.lang.Unroll

class CustomCacheKeyGeneratorSpec extends Specification {

    void 'test matching keys'() {

        given:
        CustomCacheKeyGenerator keyGenerator = new CustomCacheKeyGenerator()

        when:
        Serializable key1 = keyGenerator.generate('TestService', 'method', 0, [ arg1: 1, arg2: 2 ])
        Serializable key2 = keyGenerator.generate('TestService', 'method', 0, [ arg1: 1, arg2: 2 ])

        then:
        key1.hashCode() == key2.hashCode()
    }

    @Unroll('#className::#methodName(#params) should not match TestService::method([arg1: 1, arg2: 2])')
    void 'test differing keys'() {

        given:
        CustomCacheKeyGenerator keyGenerator = new CustomCacheKeyGenerator()
        Serializable key1 = keyGenerator.generate('TestService', 'method', 0, [ arg1: 1, arg2: 2 ])

        when:
        Serializable key2 = keyGenerator.generate(className, methodName, 0, params)

        then:
        key1.hashCode() != key2.hashCode()

        where:
        className | methodName | params
        'TestService' | 'method' | [ arg1: 1, arg2: 3 ]
        'TestService' | 'method' | [ arg1: 1, _arg2: 2 ]
        'TestService' | '_method' | [ arg1: 1, arg2: 2 ]
        '_TestService' | 'method' | [ arg1: 1, arg2: 2 ]
    }
}
