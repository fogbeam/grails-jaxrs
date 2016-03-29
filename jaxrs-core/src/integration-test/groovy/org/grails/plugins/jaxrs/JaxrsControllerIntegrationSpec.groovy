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

import geb.spock.GebSpec
import org.grails.plugins.jaxrs.support.JerseyClientTrait
import org.grails.plugins.jaxrs.support.JerseyClientTrait.RequestProperties
import spock.lang.Unroll

/**
 * @author Noam Y. Tenne
 */
abstract class JaxrsControllerIntegrationSpec extends GebSpec implements JerseyClientTrait {
    def grailsUrlMappingsHolder
    def grailsApplication

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
        response.contentType == 'text/plain'
        response.body == 'test01'
    }

    def "Execute a POST request on resource 02"() {
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

    def "Execute a POST request on resource 03"() {
        when:
        def response = makePostRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/03",
            contentType: 'application/json',
            body: '{"age":38,"name":"mike"}'
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
        def response = makePostRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/06",
            contentType: 'application/json',
            accept: 'application/xml',
            body: '{"age":38,"name":"mike"}'
        ))

        then:
        response.status == 200
        response.contentType == 'application/xml'

        def data = parseXml(response.body)
        data['age'].text() == '39'
        data['name'].text() == 'ekim'
    }

    def "Initiate a single round-trip on resource 04 for content type application/xml"() {
        when:
        def response = makePostRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/04/single",
            contentType: 'application/xml',
            accept: 'application/xml',
            body: '<testPerson><name>james</name></testPerson>'
        ))

        then:
        response.status == 200
        response.contentType == 'application/xml'

        def data = parseXml(response.body)
        data['name'].text() == 'semaj'
    }

    @Unroll
    def "Initiate a single round-trip on resource 04 for content type application/json"() {
        when:
        def response = makePostRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/04/single",
            contentType: 'application/json',
            accept: 'application/json',
            body: '{"class":"TestPerson","age":25,"name":"james"}'
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
        def response = makeGetRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/04/multi/generic",
            accept: 'application/xml'
        ))

        then:
        response.contentType == 'application/xml'

        response.body.contains('<list>')
        response.body.contains('<name>n1</name>')
        response.body.contains('<name>n2</name>')

        where:
        genericOnly << [true, false]
    }

    @Unroll
    def "Retrieve a raw and object XML collection from resource 04 with mode #mode"() {
        when:
        def response = makeGetRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/04/multi/$mode",
            accept: 'application/xml'
        ))

        then:
        response.status == 200
        response.contentType == 'application/xml'

        response.body.contains('<list>')
        response.body.contains('<name>n1</name>')
        response.body.contains('<name>n2</name>')

        where:
        mode << ['raw', 'object']
    }

    def "Retrieve a generic JSON collection from resource 04"() {
        when:
        def response = makeGetRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/04/multi/generic",
            accept: 'application/json'
        ))

        then:
        response.contentType == 'application/json'
        response.body.contains('"name":"n1"')
        response.body.contains('"name":"n2"')
    }

    def "Retrieve the default response of a POST request to resource 04"() {
        when:
        def response = makePostRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/04/single",
            contentType: 'application/xml',
            body: '<testPerson><name>james</name></testPerson>'
        ))

        then:
        response.status == 200
        response.body == '{"age":1,"id":null,"name":"semaj","version":null}'
        response.contentType == 'application/json'
    }

    def "Retrieve an HTML response from resource 05"() {
        when:
        def response = makeGetRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/05"
        ))

        then:
        response.status == 200
        response.body == '<html><body>test05</body></html>'
        response.contentType == 'text/html'
    }

    def "Specify query params in the request path"() {
        when:
        def response = makeGetRequest(new RequestProperties(
            uri: "${browser.baseUrl}/test/queryParam?value=jim"
        ))

        then:
        response.status == 200
        response.body == 'jim'
    }
}
