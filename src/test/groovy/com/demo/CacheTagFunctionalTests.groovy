package com.demo

import functionaltestplugin.FunctionalTestCase

class CacheTagFunctionalTests extends FunctionalTestCase {

    protected void setUp() {
        super.setUp()

        get '/demo/clearBlocksCache'
        assertStatus 200
        assertContentContains 'cleared blocks cache'

        get '/demo/clearTemplatesCache'
        assertStatus 200
        assertContentContains 'cleared templates cache'
    }

    void testBlockTag() {
        get '/demo/blockCache?counter=5'
        assertStatus 200
        assertContentContains 'First block counter 6'
        assertContentContains 'Second block counter 7'
        assertContentContains 'Third block counter 8'

        get '/demo/blockCache?counter=42'
        assertStatus 200
        assertContentContains 'First block counter 6'
        assertContentContains 'Second block counter 7'
        assertContentContains 'Third block counter 8'
    }

    void testClearingBlocksCache() {
        get '/demo/blockCache?counter=100'
        assertStatus 200
        assertContentContains 'First block counter 101'
        assertContentContains 'Second block counter 102'
        assertContentContains 'Third block counter 103'

        get '/demo/blockCache?counter=42'
        assertStatus 200
        assertContentContains 'First block counter 101'
        assertContentContains 'Second block counter 102'
        assertContentContains 'Third block counter 103'

        get '/demo/clearBlocksCache'
        assertStatus 200
        assertContentContains 'cleared blocks cache'

        get '/demo/blockCache?counter=50'
        assertStatus 200
        assertContentContains 'First block counter 51'
        assertContentContains 'Second block counter 52'
        assertContentContains 'Third block counter 53'

        get '/demo/blockCache?counter=150'
        assertStatus 200
        assertContentContains 'First block counter 51'
        assertContentContains 'Second block counter 52'
        assertContentContains 'Third block counter 53'
    }

    void testRenderTag() {
        get '/demo/renderTag?counter=1'
        assertStatus 200

        assertContentContains 'First invocation: Counter value: 1'
        assertContentContains 'Second invocation: Counter value: 1'
        assertContentContains 'Third invocation: Counter value: 3'
        assertContentContains 'Fourth invocation: Counter value: 3'
        assertContentContains 'Fifth invocation: Counter value: 1'

        get '/demo/renderTag?counter=5'
        assertStatus 200

        assertContentContains 'First invocation: Counter value: 1'
        assertContentContains 'Second invocation: Counter value: 1'
        assertContentContains 'Third invocation: Counter value: 3'
        assertContentContains 'Fourth invocation: Counter value: 3'
        assertContentContains 'Fifth invocation: Counter value: 1'

        get '/demo/clearTemplatesCache'
        assertStatus 200
        assertContentContains 'cleared templates cache'

        get '/demo/renderTag?counter=5'
        assertStatus 200

        assertContentContains 'First invocation: Counter value: 5'
        assertContentContains 'Second invocation: Counter value: 5'
        assertContentContains 'Third invocation: Counter value: 7'
        assertContentContains 'Fourth invocation: Counter value: 7'
        assertContentContains 'Fifth invocation: Counter value: 5'

        get '/demo/renderTag?counter=1'
        assertStatus 200

        assertContentContains 'First invocation: Counter value: 5'
        assertContentContains 'Second invocation: Counter value: 5'
        assertContentContains 'Third invocation: Counter value: 7'
        assertContentContains 'Fourth invocation: Counter value: 7'
        assertContentContains 'Fifth invocation: Counter value: 5'
    }
}
