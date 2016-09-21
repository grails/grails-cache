package grails.plugin.cache.util

import spock.lang.Specification

class CacheConditionUtilsSpec extends Specification {

    void 'test condition expression parser'() {
        expect:
        CacheConditionUtils.shouldCache(null, [x: 10], '#x > 4')
        !CacheConditionUtils.shouldCache(null, [x: 2], '#x > 4')
    }

    void 'test condition expression parser with expression which references a property'() {
        given:
        def w = new Widget(width: 10)

        expect:
        CacheConditionUtils.shouldCache(w, [x: 12], '#this.width > 5')
        !CacheConditionUtils.shouldCache(w, [x: 12], '#this.width > 25')
    }
}

class Widget {
    int width
}
