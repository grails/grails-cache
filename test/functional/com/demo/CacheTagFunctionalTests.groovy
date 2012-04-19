package com.demo

class CacheTagFunctionalTests extends functionaltestplugin.FunctionalTestCase {

    void testBlockTag() {
        get '/demo/cacheTagBasics?counter=5'
        assertStatus 200
        assertContentContains 'First block counter 6'
        assertContentContains 'Third block counter 7'
        assertContentContains 'Sixth block counter 8'

        assertContentDoesNotContain 'Second'
        assertContentDoesNotContain 'Fourth'
        assertContentDoesNotContain 'Fifth'

        get '/demo/cacheTagBasics?counter=14'
        assertStatus 200
        assertContentContains 'First block counter 6'
        assertContentContains 'Third block counter 7'
        assertContentContains 'Sixth block counter 8'

        assertContentDoesNotContain 'Second'
        assertContentDoesNotContain 'Fourth'
        assertContentDoesNotContain 'Fifth'
    }

    void testBlockTagWithMapsAsKeys() {
        get '/demo/mapAsKey?counter=18'
        assertStatus 200
        assertContentContains 'First block counter 19'
        assertContentContains 'Third block counter 20'
        assertContentContains 'Sixth block counter 21'

        assertContentDoesNotContain 'Second'
        assertContentDoesNotContain 'Fourth'
        assertContentDoesNotContain 'Fifth'

        get '/demo/mapAsKey?counter=14'
        assertStatus 200
        assertContentContains 'First block counter 19'
        assertContentContains 'Third block counter 20'
        assertContentContains 'Sixth block counter 21'

        assertContentDoesNotContain 'Second'
        assertContentDoesNotContain 'Fourth'
        assertContentDoesNotContain 'Fifth'
    }
    
    void testRenderTag() {
        get '/demo/renderTag'
        assertStatus 200
        
        assertContentContains 'First invocation: Counter value: 1'
        assertContentContains 'Second invocation: Counter value: 1'
        assertContentContains 'Third invocation: Counter value: 3'
        assertContentContains 'Fourth invocation: Counter value: 3'
        assertContentContains 'Fifth invocation: Counter value: 1'
    }
}