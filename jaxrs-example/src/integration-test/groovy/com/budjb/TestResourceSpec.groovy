package com.budjb

import grails.test.mixin.integration.Integration
import org.grails.plugins.jaxrs.test.JaxrsIntegrationSpec
import org.grails.plugins.jaxrs.test.JaxrsRequestProperties

@Integration
class TestResourceSpec extends JaxrsIntegrationSpec {
    def 'Ensure GET /api/test returns the correct content'() {
        when:
        def response = makeRequest(new JaxrsRequestProperties(method: 'GET', uri: '/api/test'))

        then:
        response.bodyAsString == 'Test'
        response.status == 200
    }

    /**
     * Return the list of additional resources to build the JAX-RS servlet with.
     *
     * @return
     */
    @Override
    List getResources() {
        return []
    }
}
