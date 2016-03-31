package org.grails.plugins.jaxrs

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.grails.plugins.jaxrs.core.JaxrsServletConfig
import org.grails.plugins.jaxrs.servlet.ServletFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.servlet.Servlet

/**
 * Main application class. Note that this only gets run if the plugin
 * is started directly or through running the test suite.
 */
@Configuration
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    /**
     * Creates a null servlet factory. This method will only run if the jaxrs-core
     * plugin is run directly, such as during tests. Without this, the environment
     * will not start.
     *
     * Since this is only run during tests, applications that include only this plugin
     * will still see a failure due to the <code>jaxrsServletFactory</code> bean not
     * having been defined.
     *
     * @return
     */
    @Bean
    ServletFactory jaxrsServletFactory() {
        return new ServletFactory() {
            @Override
            Servlet createServlet(JaxrsApplicationConfig applicationConfig, JaxrsServletConfig servletConfig) {
                return null
            }

            @Override
            String getRuntimeDelegateClassName() {
                return null
            }
        }
    }
}
