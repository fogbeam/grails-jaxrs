package org.grails.plugins.jaxrs.test

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpServletResponse

class JaxrsResponseProperties {
    /**
     * HTTP status of the response.
     */
    int status

    /**
     * Body of the response in bytes.
     */
    byte[] body

    /**
     * Content type of the response.
     */
    String contentType

    /**
     * Response headers.
     */
    Map<String, List<Object>> headers = [:]

    /**
     * Constructor that bases the class properties on the given response object.
     *
     * @param httpServletResponse
     */
    JaxrsResponseProperties(MockHttpServletResponse httpServletResponse) {
        status = httpServletResponse.getStatus()
        body = httpServletResponse.getContentAsByteArray()
        contentType = httpServletResponse.getContentType()
        httpServletResponse.getHeaderNames().each {
            headers.put(it, httpServletResponse.getHeaderValues(it))
        }
    }

    /**
     * Return the body as a string.
     *
     * @return
     */
    String getBodyAsString() {
        return new String(body)
    }

    /**
     * Return the body as JSON.
     *
     * @return
     */
    def getBodyAsJson() {
        return new JsonSlurper().parse(body)
    }

    /**
     * Return the body as XML.
     *
     * @return
     */
    GPathResult getBodyAsXml() {
        return new XmlSlurper().parseText(getBodyAsString())
    }
}
