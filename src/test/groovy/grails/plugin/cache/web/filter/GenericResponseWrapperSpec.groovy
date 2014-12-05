package grails.plugin.cache.web.filter

import grails.plugin.cache.SerializableByteArrayOutputStream
import grails.plugin.cache.web.GenericResponseWrapper
import spock.lang.Specification
import org.codehaus.groovy.grails.plugins.testing.*

/**
 * Created by graemerocher on 09/05/14.
 */
class GenericResponseWrapperSpec extends Specification{

    void "Test that generic response wrapper buffers the response correctly"() {
        when:"A generic response wrapper is created and used"
            SerializableByteArrayOutputStream out = new SerializableByteArrayOutputStream();
            def wrapper = new GenericResponseWrapper(new GrailsMockHttpServletResponse(), out)

            wrapper.writer << "write me"
            wrapper.writer.flush()

        then:"The result is correct"
            out.toString() == 'write me'


    }
}
