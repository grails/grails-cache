package com.demo

class CacheTagFunctionalTests extends functionaltestplugin.FunctionalTestCase {

    void testCacheTag() {
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

    void testCacheTagWithMapsAsKeys() {
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
}