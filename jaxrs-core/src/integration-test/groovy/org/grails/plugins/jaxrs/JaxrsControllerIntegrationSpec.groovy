/*
 * Copyright 2009 - 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.plugins.jaxrs

import grails.test.mixin.integration.Integration
import org.grails.plugins.jaxrs.resources.*
import org.grails.plugins.jaxrs.support.MockedEnvironmentSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

/**
 * @author Noam Y. Tenne
 */
@Integration
class JaxrsControllerIntegrationSpec extends MockedEnvironmentSpec {
    /**
     * Grails application bean.
     */
    def grailsApplication

    /**
     * Return the list of resources to build the JAX-RS servlet with.
     *
     * @return
     */
    @Override
    List getResources() {
        return [
            CustomResponseEntityWriterProvider,
            CustomRequestEntityReaderProvider,
            Test01Resource,
            Test02Resource,
            Test03Resource,
            Test04Resource,
            Test05Resource,
            Test06Resource,
            TestQueryParamResource
        ]
    }

    def "Execute a GET request"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/01',
            method: 'GET'
        ))

        then:
        response.status == 200
        response.contentType == 'text/plain'
        response.bodyAsString == 'test01'
    }

    def "Execute a POST request on resource 02"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/02',
            method: 'POST',
            contentType: 'text/plain',
            body: 'hello'.bytes
        ))

        then:
        response.status == 200
        response.contentType == 'text/plain'
        response.bodyAsString == 'response:hello'
    }

    def "Execute a POST request on resource 03"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/03',
            method: 'POST',
            contentType: 'application/json',
            body: '{"age":38,"name":"mike"}'.bytes
        ))

        then:
        response.status == 200
        response.contentType == 'application/json'

        Map data = parseJson(response.body) as Map
        data.age == 39
        data.name == "ekim"
    }

    def "Execute a POST request on resource 06"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/06',
            method: 'POST',
            contentType: 'application/json',
            accept: 'application/xml',
            body: '{"age":38,"name":"mike"}'.bytes
        ))

        then:
        response.status == 200
        response.contentType == 'application/xml'

        def data = parseXml(response.bodyAsString)
        data['age'].text() == '39'
        data['name'].text() == 'ekim'
    }

    def "Initiate a single round-trip on resource 04 for content type application/xml"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/04/single',
            method: 'POST',
            contentType: 'application/xml',
            accept: 'application/xml',
            body: '<testPerson><name>james</name></testPerson>'.bytes
        ))

        then:
        response.status == 200
        response.contentType == 'application/xml'

        def data = parseXml(response.bodyAsString)
        data['name'].text() == 'semaj'
    }

    def "Initiate a single round-trip on resource 04 for content type application/json"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/04/single',
            method: 'POST',
            contentType: 'application/json',
            accept: 'application/json',
            body: '{"class":"TestPerson","age":25,"name":"james"}'.bytes
        ))

        then:
        response.status == 200
        response.contentType == 'application/json'

        def data = parseJson(response.body)
        data.name == 'semaj'
        data.age == 26
    }

    @Unroll
    def "Retrieve a generic XML collection from resource 04 where genericOnly is #genericOnly"() {
        setup:
        grailsApplication.config.org.grails.jaxrs.dowriter.require.generic.collections = genericOnly

        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/04/multi/generic',
            method: 'GET',
            accept: 'application/xml'
        ))

        then:
        response.contentType == 'application/xml'

        response.bodyAsString.contains('<list>')
        response.bodyAsString.contains('<name>n1</name>')
        response.bodyAsString.contains('<name>n2</name>')

        where:
        genericOnly << [true, false]
    }

    @Unroll
    def "Retrieve a raw and object XML collection from resource 04 with mode #mode"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: "/test/04/multi/$mode",
            method: 'GET',
            accept: 'application/xml'
        ))

        then:
        response.status == 200
        response.contentType == 'application/xml'

        response.bodyAsString.contains('<list>')
        response.bodyAsString.contains('<name>n1</name>')
        response.bodyAsString.contains('<name>n2</name>')

        where:
        mode << ['raw', 'object']
    }

    def "Retrieve a raw and object XML collection from resource 04 with generic-only turned on"() {
        setup:
        grailsApplication.config.org.grails.jaxrs.dowriter.require.generic.collections = true

        when:
        def response = makeRequest(new RequestProperties(
            uri: "/test/04/multi/$mode",
            method: 'GET',
            accept: 'application/xml'
        ))

        then:
        response.status == 500

        cleanup:
        grailsApplication.config.org.grails.jaxrs.dowriter.require.generic.collections = false

        where:
        mode << ['raw', 'object']
    }

    def "Retrieve a generic JSON collection from resource 04"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/04/multi/generic',
            method: 'GET',
            accept: 'application/json'
        ))

        then:
        response.contentType == 'application/json'
        response.bodyAsString.contains('"name":"n1"')
        response.bodyAsString.contains('"name":"n2"')
    }

    @Unroll
    def "Post content to resource 04 while the IO facilities are disabled to test the #facilityToDisabled"() {
        setup:
        grailsApplication.config.org.grails.jaxrs.getProperty("do${facilityToDisabled}").disable = true

        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/04/single',
            method: 'POST',
            contentType: 'application/xml',
            accept: 'application/xml',
            body: '<testPerson><name>james</name></testPerson>'.bytes
        ))

        then:
        response.status == expectedStatus

        cleanup:
        grailsApplication.config.org.grails.jaxrs.getProperty("do${facilityToDisabled}").disable = false

        where:
        facilityToDisabled | expectedStatus
        'reader'           | 415
        'writer'           | 500
    }

    def "Retrieve the default response of a POST request to resource 04"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/04/single',
            method: 'POST',
            contentType: 'application/xml',
            body: '<testPerson><name>james</name></testPerson>'.bytes
        ))

        then:
        response.status == 200
        response.bodyAsString == '{"age":1,"id":null,"name":"semaj","version":null}'
        response.contentType == 'application/json'
    }

    def "Retrieve an HTML response from resource 05"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/05',
            method: 'GET'
        ))

        then:
        response.status == 200
        response.bodyAsString == '<html><body>test05</body></html>'
        response.contentType == 'text/html'
    }

    def "Specify query params in the request path"() {
        when:
        def response = makeRequest(new RequestProperties(
            uri: '/test/queryParam?value=jim',
            method: 'GET'
        ))

        then:
        response.status == 200
        response.bodyAsString == 'jim'
    }
}
