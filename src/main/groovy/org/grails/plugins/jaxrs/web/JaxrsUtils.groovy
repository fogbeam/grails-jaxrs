/*
 * Copyright 2009 the original author or authors.
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
package org.grails.plugins.jaxrs.web

import grails.core.GrailsApplication
import org.grails.plugins.jaxrs.provider.DomainObjectReader
import org.grails.plugins.jaxrs.provider.DomainObjectWriter
import org.grails.plugins.jaxrs.provider.JSONReader
import org.grails.plugins.jaxrs.provider.JSONWriter
import org.grails.plugins.jaxrs.provider.XMLReader
import org.grails.plugins.jaxrs.provider.XMLWriter
import org.springframework.context.ApplicationContext

import javax.servlet.ServletContext
import javax.servlet.ServletRequest

import static org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext

/**
 * @author Martin Krasser
 */
public class JaxrsUtils {
    /**
     * Bean name of the JAX-RS context.
     */
    static final String JAXRS_CONTEXT_NAME = "jaxrsContext"

    /**
     * Name of the request attribute for storing the request URI.
     */
    static final String REQUEST_URI_ATTRIBUTE_NAME = "org.grails.jaxrs.request.uri"

    /**
     * Obtains the request URI that has been previously been stored via
     * {@link #setRequestUriAttribute(ServletRequest, String)} from a
     * <code>request</code>.
     *
     * @param request request where to obtain the URI from.
     * @return request URI.
     */
    static String getRequestUriAttribute(ServletRequest request) {
        return (String) request.getAttribute(REQUEST_URI_ATTRIBUTE_NAME)
    }

    /**
     * Stores a request <code>uri</code> as <code>request</code> attribute. The
     * request attribute name is {@link #REQUEST_URI_ATTRIBUTE_NAME}.
     *
     * @param request request where to store the URI.
     * @param uri request URI.
     */
    static void setRequestUriAttribute(ServletRequest request, String uri) {
        request.setAttribute(REQUEST_URI_ATTRIBUTE_NAME, uri)
    }

    /**
     * Returns the {@link JaxrsContext} for the given servlet context.
     *
     * @param servletContext servlet context.
     * @return a {@link JaxrsContext} instance.
     */
    public static JaxrsContext getRequiredJaxrsContext(ServletContext servletContext) {
        return getRequiredJaxrsContext(getRequiredWebApplicationContext(servletContext))
    }

    /**
     * Returns the {@link JaxrsContext} for the given servlet context.
     *
     * @param applicationContext application context.
     * @return a {@link JaxrsContext} instance.
     */
    public static JaxrsContext getRequiredJaxrsContext(ApplicationContext applicationContext) {
        return applicationContext.getBean(JAXRS_CONTEXT_NAME, JaxrsContext)
    }

    public static void setupJaxrsContext(JaxrsContext context, GrailsApplication grailsApplication) {
        JaxrsConfig config = context.jaxrsConfig

        context.jaxrsProviderName = getProviderName(grailsApplication)
        context.jaxrsProviderExtraPaths = getProviderExtraPaths(grailsApplication)
        context.jaxrsProviderInitParameters = getProviderInitParameters(grailsApplication)

        config.reset()
        config.classes << XMLWriter
        config.classes << XMLReader
        config.classes << JSONWriter
        config.classes << JSONReader
        config.classes << DomainObjectReader
        config.classes << DomainObjectWriter

        grailsApplication.getArtefacts('Resource').each { clazz ->
            config.classes << clazz.clazz
        }
        grailsApplication.getArtefacts('Provider').each { clazz ->
            config.classes << clazz.clazz
        }
    }

    private static String getProviderName(application) {
        def name = application.config.org.grails.jaxrs.provider.name
        if (!name) {
            name = JaxrsContext.JAXRS_PROVIDER_NAME_JERSEY
        }
        return name
    }

    private static String getProviderExtraPaths(application) {
        return application.config.org.grails.jaxrs.provider.extra.paths
    }

    private static Map<String, String> getProviderInitParameters(application) {
        ConfigObject config = application.config.org.grails.jaxrs.provider.init.parameters
        return config.flatten();
    }
}
