package org.grails.plugins.jaxrs

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import org.grails.plugins.jaxrs.support.JerseyClientTrait
import org.grails.plugins.jaxrs.support.JerseyClientTrait.RequestProperties

@Integration
class ExampleIntegrationSpec extends GebSpec implements JerseyClientTrait {
    def grailsUrlMappingsHolder

    def setup() {
        grailsUrlMappingsHolder.addMappings {
            "/test"(controller: 'jaxrs')
            "/test/**"(controller: 'jaxrs')
        }
    }

    def "Execute a GET request"() {
        when:
        def response = makeGetRequest(new RequestProperties(uri: "${browser.baseUrl}/test/01"))

        then:
        response.status == 200
        response.body == 'test01'
        response.contentType == 'text/plain'
    }

    def "Execute a POST request"() {
        when:
        def response = makePostRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/02",
            contentType: 'text/plain',
            body: 'hello'
        ))

        then:
        response.status == 200
        response.contentType == 'text/plain'
        response.body == 'response:hello'
    }
}
