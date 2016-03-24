package org.grails.plugins.jaxrs.itest

import geb.spock.GebReportingSpec
import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

@Integration
class BudSpec extends GebSpec {
    def grailsUrlMappingsHolder

    @Shared
    RESTClient restClient

    def setupSpec() {
        restClient = new RESTClient()
    }

    def setup() {
        grailsUrlMappingsHolder.addMappings({
            "/test"(controller: 'jaxrs')
            "/test/**"(controller: 'jaxrs')
        })
    }

    def 'does this shit work?'() {
        when:
        def resp = restClient.get(uri: "${browser.baseUrl}/test/01")

        then:
        resp.status == 200
        resp.data == 'test01'
        resp.contentType == 'text/plain'
    }
}
