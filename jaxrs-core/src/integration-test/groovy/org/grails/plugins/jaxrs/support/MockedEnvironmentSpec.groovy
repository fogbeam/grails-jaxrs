package org.grails.plugins.jaxrs.support

import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.api.core.ResourceConfig
import com.sun.jersey.spi.container.servlet.WebConfig
import com.sun.jersey.spi.spring.container.servlet.SpringServlet
import grails.test.mixin.integration.Integration
import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang.StringUtils
import org.grails.plugins.jaxrs.JaxrsController
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.grails.plugins.jaxrs.core.JaxrsContext
import org.grails.plugins.jaxrs.core.JaxrsServletConfig
import org.grails.plugins.jaxrs.core.JaxrsUtil
import org.grails.plugins.jaxrs.provider.JSONReader
import org.grails.plugins.jaxrs.provider.JSONWriter
import org.grails.plugins.jaxrs.provider.XMLReader
import org.grails.plugins.jaxrs.provider.XMLWriter
import org.grails.plugins.jaxrs.servlet.ServletFactory
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

import javax.servlet.Servlet
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.HttpHeaders

@Integration
abstract class MockedEnvironmentSpec extends Specification {
    /**
     * JAX-RS controller.
     */
    JaxrsController jaxrsController

    /**
     * JAX-RS context.
     */
    JaxrsContext jaxrsContext

    /**
     * JAX-RS utility.
     */
    JaxrsUtil jaxrsUtil

    /**
     * Servlet context.
     */
    ServletContext servletContext

    /**
     * Servlet factory.
     */
    ServletFactory servletFactory

    @Autowired
    ApplicationContext applicationContext

    /**
     * Set up the environment for tests.
     */
    void setup() {
        JaxrsApplicationConfig application = new JaxrsApplicationConfig()
        application.classes.addAll(getResources())

        servletFactory = new ServletFactory() {
            @Override
            Servlet createServlet(JaxrsApplicationConfig applicationConfig, JaxrsServletConfig servletConfig) {
                return new SpringServlet() {
                    @Override
                    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props,
                                                                      WebConfig webConfig) throws ServletException {
                        return new DefaultResourceConfig(applicationConfig.classes)
                    }
                }
            }

            @Override
            String getRuntimeDelegateClassName() {
                return "com.sun.jersey.server.impl.provider.RuntimeDelegateImpl"
            }
        }

        servletContext = new MockServletContext()
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext)

        jaxrsContext = new JaxrsContext()
        jaxrsContext.servletContext = servletContext
        jaxrsContext.jaxrsServletFactory = servletFactory
        jaxrsContext.applicationConfig = application

        jaxrsUtil = new JaxrsUtil()
        jaxrsUtil.jaxrsContext = jaxrsContext

        jaxrsController = new JaxrsController()
        jaxrsController.jaxrsContext = jaxrsContext
        jaxrsController.jaxrsUtil = jaxrsUtil

        jaxrsContext.init()
    }

    /**
     * Teardown the test environment.
     */
    void cleanup() {
        clearRequestContextHolder()
    }

    /**
     * Reset request context holder.
     */
    void clearRequestContextHolder() {
        RequestContextHolder.resetRequestAttributes()
    }

    /**
     * Set up the request context holder.
     */
    void createRequestContextHolder(HttpServletRequest request, HttpServletResponse response) {
        GrailsWebRequest webRequest = new GrailsWebRequest(request, response, servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        RequestContextHolder.setRequestAttributes(webRequest)
    }

    /**
     * Make a JAX-RS request.
     *
     * @param requestProperties
     * @return
     */
    ResponseProperties makeRequest(RequestProperties requestProperties) {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest(servletContext)

        httpServletRequest.characterEncoding = 'UTF-8'

        URI uri = new URI(requestProperties.uri)
        jaxrsUtil.setRequestUriAttribute(httpServletRequest, uri.path)

        if (uri.query) {
            httpServletRequest.queryString = uri.query
        }

        httpServletRequest.method = requestProperties.method
        httpServletRequest.content = requestProperties.body

        requestProperties.headers.each { entry ->
            httpServletRequest.addHeader(entry.key, entry.value)
        }

        httpServletRequest.setContentType(requestProperties.contentType)

        if (requestProperties.accept) {
            httpServletRequest.addHeader('Accept', requestProperties.accept)
        }

        if (requestProperties.body?.size()) {
            String existingContentLength = httpServletRequest.getHeader(HttpHeaders.CONTENT_LENGTH)
            if (StringUtils.isBlank(existingContentLength)) {
                httpServletRequest.addHeader(HttpHeaders.CONTENT_LENGTH, requestProperties.body.size())
            }
        }

        HttpServletResponse httpServletResponse = new MockHttpServletResponse()

        createRequestContextHolder(httpServletRequest, httpServletResponse)

        jaxrsController.handle()

        ResponseProperties responseProperties = new ResponseProperties()
        responseProperties.status = httpServletResponse.getStatus()
        responseProperties.body = httpServletResponse.getContentAsByteArray()
        responseProperties.contentType = httpServletResponse.getContentType()

        return responseProperties

    }

    /**
     * Return the list of resources to build the JAX-RS servlet with.
     *
     * @return
     */
    abstract List getResources()

    /**
     * Parses a JSON string into a list or map.
     *
     * @param raw
     * @return
     */
    def parseJson(String raw) {
        return new JsonSlurper().parseText(raw)
    }

    /**
     * Parses a JSON bytestream into a list or map.
     *
     * @param raw
     * @return
     */
    def parseJson(byte[] raw) {
        return new JsonSlurper().parse(raw)
    }

    /**
     * Parses a String into an XML structure.
     *
     * @param raw
     * @return
     */
    GPathResult parseXml(String raw) {
        return new XmlSlurper().parseText(raw)
    }

    /**
     * Used in the process of making requests.
     */
    static class RequestProperties {
        String uri
        String method
        byte[] body
        String contentType
        String accept
        Map<String, String> headers = [:]
    }

    /**
     * Returned from a request.
     */
    static class ResponseProperties {
        int status
        byte[] body
        String contentType

        String getBodyAsString() {
            return new String(body)
        }
    }
}
