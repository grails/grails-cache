package com.demo

class CacheTagFunctionalTests extends functionaltestplugin.FunctionalTestCase {

    void testBlockTag() {
        get '/demo/cacheTagBasics?counter=5'
        assertStatus 200
        assertContentContains 'First block counter 6'
        assertContentContains 'Second block counter 7'
        assertContentContains 'Third block counter 8'
        
        get '/demo/cacheTagBasics?counter=42'
        assertStatus 200
        assertContentContains 'First block counter 6'
        assertContentContains 'Second block counter 7'
        assertContentContains 'Third block counter 8'
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