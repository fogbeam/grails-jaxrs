package org.grails.plugins.jaxrs.test

import org.grails.plugins.jaxrs.JaxrsController
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.grails.plugins.jaxrs.core.JaxrsContext
import org.grails.plugins.jaxrs.core.JaxrsUtil
import org.grails.plugins.jaxrs.servlet.ServletFactory
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class JaxrsIntegrationSpec extends Specification {
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
     *
     * This allows the mocked environment to use the real servlet to service requests.
     */
    @Autowired
    ServletFactory servletFactory

    /**
     * Application context.
     */
    @Autowired
    ApplicationContext applicationContext

    /**
     * Set up the environment for tests.
     */
    void setup() {
        JaxrsApplicationConfig application = new JaxrsApplicationConfig()
        application.classes.addAll(getResources())

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
        RequestContextHolder.resetRequestAttributes()
    }

    /**
     * Make a JAX-RS request.
     *
     * @param requestProperties
     * @return
     */
    JaxrsResponseProperties makeRequest(JaxrsRequestProperties requestProperties) {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse()
        HttpServletRequest httpServletRequest = requestProperties.createServletRequest(servletContext)

        GrailsWebRequest webRequest = new GrailsWebRequest(httpServletRequest, httpServletResponse, servletContext)
        httpServletRequest.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        RequestContextHolder.setRequestAttributes(webRequest)

        jaxrsController.handle()

        return new JaxrsResponseProperties(httpServletResponse)
    }

    /**
     * Return the list of resources to build the JAX-RS servlet with.
     *
     * @return
     */
    abstract List getResources()
}
