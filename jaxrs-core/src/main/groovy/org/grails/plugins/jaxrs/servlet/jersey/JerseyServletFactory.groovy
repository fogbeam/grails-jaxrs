package org.grails.plugins.jaxrs.servlet.jersey

import com.sun.jersey.api.core.PackagesResourceConfig
import com.sun.jersey.spi.spring.container.servlet.SpringServlet
import grails.core.GrailsApplication
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.grails.plugins.jaxrs.core.JaxrsServletConfig
import org.grails.plugins.jaxrs.servlet.ServletFactory

import javax.servlet.Servlet

class JerseyServletFactory implements ServletFactory {
    /**
     * Grails Application bean.
     */
    GrailsApplication grailsApplication

    /**
     * Create the servlet instance for the implementation-specific provider.
     *
     * @param applicationConfig
     * @param servletConfig
     * @return
     */
    @Override
    Servlet createServlet(JaxrsApplicationConfig applicationConfig, JaxrsServletConfig servletConfig) {
        setupServletConfig(servletConfig)

        return new SpringServlet()
    }

    /**
     * Returns the fully qualified name of the class that will serve as the runtime delegate
     * for JAX-RS.
     *
     * @return
     */
    @Override
    String getRuntimeDelegateClassName() {
        return "com.sun.jersey.server.impl.provider.RuntimeDelegateImpl"
    }

    /**
     * Add additional servlet configuration options specific to Jersey.
     *
     * @param servletConfig
     */
    void setupServletConfig(JaxrsServletConfig servletConfig) {
        String extra = getProviderExtraPaths()

        if (extra && !servletConfig.getInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES)) {
            servletConfig.getInitParameters().put(PackagesResourceConfig.PROPERTY_PACKAGES, extra)
        }
    }

    /**
     * Returns any extra classpaths configured by the application.
     *
     * @param application
     * @return
     */
    String getProviderExtraPaths() {
        // TODO: change this path to be jersey-specific
        def paths = grailsApplication.config.org.grails.jaxrs.provider.extra.paths

        if (!(paths instanceof String) || !paths) {
            return null
        }

        return paths
    }
}
