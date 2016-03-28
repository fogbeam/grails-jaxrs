/*
 * Copyright 2009-2010 the original author or authors.
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

import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.api.core.PackagesResourceConfig
import com.sun.jersey.api.core.ResourceConfig
import com.sun.jersey.spi.container.servlet.WebConfig
import com.sun.jersey.spi.spring.container.servlet.SpringServlet

import javax.servlet.ServletException
import javax.ws.rs.ext.Provider

/**
 * Servlet that dispatches JAX-RS requests to Jersey.
 *
 * @author Martin Krasser
 * @author David Castro
 */
public class JerseyServlet extends SpringServlet {
    /**
     * Application configuration.
     */
    private JaxrsApplicationConfig jaxrsConfig

    /**
     * Constructor.
     *
     * @param jaxrsConfig
     */
    JerseyServlet(JaxrsApplicationConfig jaxrsConfig) {
        this.jaxrsConfig = jaxrsConfig
    }

    /**
     * Sets the <code>com.sun.jersey.config.property.packages</code> init
     * parameter to the extra path defined by <code>config</code>. The extra
     * path is a semicolon-delimited list of packages which Jersey should scan
     * for additional {@link Provider} classes.
     *
     * @see JaxrsServletConfig#getExtraClassPaths()
     */
    void init(JaxrsServletConfig servletConfig) {
        String extra = servletConfig.getExtraClassPaths()

        if (extra && !servletConfig.getInitParameters().containsKey(PackagesResourceConfig.PROPERTY_PACKAGES)) {
            servletConfig.getInitParameters().put(PackagesResourceConfig.PROPERTY_PACKAGES, extra)
        }

        super.init(servletConfig)
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
        return new DefaultResourceConfig(jaxrsConfig.getClasses())
    }
}
