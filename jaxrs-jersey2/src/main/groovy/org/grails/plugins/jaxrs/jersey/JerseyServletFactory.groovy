package org.grails.plugins.jaxrs.jersey

import grails.core.GrailsApplication
import org.glassfish.jersey.servlet.ServletContainer
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.grails.plugins.jaxrs.core.JaxrsServletConfig
import org.grails.plugins.jaxrs.servlet.ServletFactory

import javax.servlet.Servlet

/**
 * A servlet factory that handles the Restlet JAX-RS implementation.
 *
 * @author Bud Byrd
 */
class JerseyServletFactory implements ServletFactory {
    /**
     * Grails application bean.
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

        return new ServletContainer()
    }

    /**
     * Returns the fully qualified name of the class that will serve as the runtime delegate
     * for JAX-RS.
     *
     * @return
     */
    @Override
    String getRuntimeDelegateClassName() {
        return 'org.glassfish.jersey.server.internal.RuntimeDelegateImpl'
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
