/*
 * Copyright 2009,2016 the original author or authors.
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
package org.grails.plugins.jaxrs.core

import javax.servlet.Servlet
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.ext.RuntimeDelegate

/**
 * The JAX-RS context used by applications to interact with a JAX-RS
 * implementation.
 *
 * @author Martin Krasser
 * @author David Castro
 * @author Bud Byrd
 */
class JaxrsContext {
    /**
     * Name of the Jersey JAX-RS implementation.
     */
    static final String JAXRS_PROVIDER_NAME_JERSEY = "jersey"

    /**
     * Name of the Restlet JAX-RS implementation.
     */
    static final String JAXRS_PROVIDER_NAME_RESTLET = "restlet"

    /**
     * Name of the JAX-RS servlet.
     */
    static final String SERVLET_NAME = "org.grails.jaxrs.servlet.name"

    /**
     * Instance of the servlet handling JAX-RS requests.
     */
    volatile Servlet jaxrsServlet

    /**
     * Application's servlet context to use with the JAX-RS servlet.
     */
    volatile ServletContext servletContext

    /**
     * JAX-RS application configuration.
     */
    volatile JaxrsApplicationConfig applicationConfig = new JaxrsApplicationConfig()

    /**
     * Which JAX-RS implementation to use.
     */
    volatile String providerName = JAXRS_PROVIDER_NAME_JERSEY

    /**
     * Extra classpaths to search for providers.
     */
    volatile String providerExtraPaths

    /**
     * Initialization parameters to pass to the JAX-RS implementation.
     */
    volatile Map<String, String> providerInitParameters = [:]

    /**
     * Reloads the JAX-RS configuration defined by Grails applications and
     * re-initializes the JAX-RS runtime.
     *
     * @throws ServletException
     * @throws IOException
     */
    void restart() throws ServletException, IOException {
        destroy()
        init()
    }

    /**
     * Initializes the JAX-RS runtime.
     *
     * @throws ServletException
     */
    void init() throws ServletException {
        if (jaxrsServlet) {
            throw new IllegalStateException("can not start the JAX-RS servlet because has already been started")
        }

        RuntimeDelegate.setInstance(null)

        Servlet servlet

        switch (providerName) {
            case JAXRS_PROVIDER_NAME_RESTLET:
                System.setProperty(
                    "javax.ws.rs.ext.RuntimeDelegate",
                    "org.restlet.ext.jaxrs.internal.spi.RuntimeDelegateImpl"
                )
                servlet = new RestletServlet(applicationConfig)
                break

            case JAXRS_PROVIDER_NAME_JERSEY:
                System.setProperty(
                    "javax.ws.rs.ext.RuntimeDelegate",
                    "com.sun.jersey.server.impl.provider.RuntimeDelegateImpl"
                )
                servlet = new JerseyServlet(applicationConfig)
                break

            default:
                throw new ServletException(
                    "Illegal provider name: ${providerName}. either use '${JAXRS_PROVIDER_NAME_JERSEY}' or " +
                        "'${JAXRS_PROVIDER_NAME_RESTLET}'."
                )
        }

        initServlet(servlet)
    }

    /**
     * Stores and initializes a JAX-RS servlet.
     *
     * @param servlet
     * @throws ServletException
     */
    void initServlet(Servlet servlet) throws ServletException {
        jaxrsServlet = servlet
        jaxrsServlet.init(createServletConfig())
    }

    /**
     * Create the servlet configuration.
     *
     * @return
     */
    JaxrsServletConfig createServletConfig() {
        return new JaxrsServletConfig(
            servletContext,
            SERVLET_NAME,
            providerExtraPaths,
            new Hashtable<String, String>(providerInitParameters)
        )
    }

    /**
     * Destroys and removes the current JAX-RS servlet, if one exists.
     */
    void destroy() {
        if (jaxrsServlet != null) {
            jaxrsServlet.destroy()
            jaxrsServlet = null
        }
    }

    /**
     * Processes an incoming request through the JAX-RS servlet.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!jaxrsServlet) {
            throw new IllegalStateException("can not service a JAX-RS request because no servlet has been started")
        }
        jaxrsServlet.service(request, response)
    }
}
