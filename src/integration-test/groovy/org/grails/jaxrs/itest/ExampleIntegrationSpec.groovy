package org.grails.jaxrs.itest

import grails.test.mixin.Mock
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.grails.jaxrs.test.CustomRequestEntityReader
import org.grails.jaxrs.test.CustomResponseEntityWriter
import org.grails.jaxrs.test.TestResource01
import org.grails.jaxrs.test.TestResource02
import org.springframework.context.ApplicationContext

/**
 * @author Noam Y. Tenne
 */
@Integration
@Rollback
@Mock(ApplicationContext)
class ExampleIntegrationSpec extends IntegrationTestSpec {

    List getJaxrsClasses() {
        [TestResource01,
                TestResource02,
                CustomRequestEntityReader,
                CustomResponseEntityWriter]
    }

    def "Execute a GET request"() {
        when:
        sendRequest('/test/01', 'GET')

        then:
        response.status == 200
        response.contentAsString == 'test01'
        response.getHeader('Content-Type').startsWith('text/plain')
    }

    def "Execute a POST request"() {
        when:
        sendRequest('/test/02', 'POST', ['Content-Type': 'text/plain'], 'hello'.bytes)

        then:
        response.status == 200
        response.contentAsString == 'response:hello'
        response.getHeader('Content-Type').startsWith('text/plain')
    }
}
