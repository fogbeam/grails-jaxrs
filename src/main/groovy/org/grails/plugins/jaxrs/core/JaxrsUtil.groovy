package org.grails.plugins.jaxrs.core

import grails.core.GrailsApplication
import grails.util.Holders
import org.grails.plugins.jaxrs.artefact.ResourceArtefactHandler
import org.grails.plugins.jaxrs.provider.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.ServletResponseWrapper
import javax.ws.rs.Path

/**
 * Provides utility methods to the JAX-RS plugin.
 *
 * @author Bud Byrd
 */
class JaxrsUtil {
    /**
     * Name of the spring bean this class should be registered as.
     */
    static final String BEAN_NAME = 'jaxrsUtil'

    /**
     * Name of the request attribute for storing the request URI.
     */
    static final String REQUEST_URI_ATTRIBUTE_NAME = 'org.grails.jaxrs.request.uri'

    /**
     * Singleton instance of this utility class.
     */
    static JaxrsUtil _instance

    /**
     * JAX-RS context instance.
     */
    JaxrsContext jaxrsContext

    /**
     * Returns the singleton instance of this class from the Spring application context.
     *
     * @return
     */
    static JaxrsUtil getInstance() {
        if (_instance) {
            return _instance
        }
        return getInstance(Holders.applicationContext)
    }

    /**
     * Returns the singleton instance of this class from the Spring application context.
     *
     * @param applicationContext
     * @return
     */
    static JaxrsUtil getInstance(ApplicationContext applicationContext) {
        if (!_instance) {
            _instance = applicationContext.getBean(BEAN_NAME, JaxrsUtil)
        }
        return _instance
    }

    /**
     * Returns the singleton instance of this class from the Spring application context.
     *
     * @param servletContext
     * @return
     */
    static JaxrsUtil getInstance(ServletContext servletContext) {
        if (_instance) {
            return _instance
        }
        return getInstance(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext))
    }

    /**
     * Grails application bean.
     */
    GrailsApplication grailsApplication

    /**
     * Logger.
     */
    Logger log = LoggerFactory.getLogger(JaxrsUtil)

    /**
     * Inspects every registered resource class for its root path, and
     * parses the path for use with URL mappings.
     *
     * @return A list of root paths used for URL mappings.
     */
    List<String> retrieveRootPathList() {
        List<String> rootPaths = []

        grailsApplication.getArtefacts(ResourceArtefactHandler.TYPE)*.clazz.each {
            try {
                String path = it.getAnnotation(Path).value()

                List tokens = path.tokenize('/')

                if (tokens.size()) {
                    rootPaths << "/${tokens[0]}"
                }
            }
            catch (NullPointerException e) {
                log.warn("Resource class '${it.name} has no root path defined", e)
            }
        }

        return rootPaths
    }

    /**
     * A helper method that unwraps a request until all wrappers are removed.
     *
     * @param servletResponse
     * @return
     */
    ServletResponse unwrap(ServletResponse servletResponse) {
        if (servletResponse instanceof ServletResponseWrapper) {
            return unwrap(servletResponse.response)
        }
        return servletResponse
    }

    /**
     * Configures the JAX-RS Application configuration by adding built-in providers
     * and scanning Grails artefacts for additional providers and resources that
     * should be included.
     *
     * @param context
     * @param grailsApplication
     */
    void setupJaxrsContext() {
        jaxrsContext.providerName = getProviderName()
        jaxrsContext.providerExtraPaths = getProviderExtraPaths()
        jaxrsContext.providerInitParameters = getProviderInitParameters()

        JaxrsApplicationConfig config = jaxrsContext.applicationConfig

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

    /**
     * Returns the configured servlet provider to use for JAX-RS.
     *
     * @param application
     * @return
     */
    String getProviderName() {
        def name = grailsApplication.config.org.grails.jaxrs.provider.name

        if (!(name instanceof String) || !name) {
            return JaxrsContext.JAXRS_PROVIDER_NAME_JERSEY
        }

        return name
    }

    /**
     * Returns any extra classpaths configured by the application.
     *
     * @param application
     * @return
     */
    String getProviderExtraPaths() {
        def paths = grailsApplication.config.org.grails.jaxrs.provider.extra.paths

        if (!(paths instanceof String) || !paths) {
            return null
        }

        return paths
    }

    /**
     * Returns any extra provider initialization paramters configured by the application.
     *
     * @param application
     * @return
     */
    Map<String, String> getProviderInitParameters() {
        ConfigObject config = grailsApplication.config.org.grails.jaxrs.provider.init.parameters

        return config.flatten()
    }

    /**
     * Obtains the request URI that has been previously been stored via
     * {@link #setRequestUriAttribute(javax.servlet.ServletRequest, String)} from a
     * <code>request</code>.
     *
     * @param request request where to obtain the URI from.
     * @return request URI.
     */
    String getRequestUriAttribute(ServletRequest request) {
        return (String) request.getAttribute(REQUEST_URI_ATTRIBUTE_NAME)
    }

    /**
     * Stores a request <code>uri</code> as <code>request</code> attribute. The
     * request attribute name is {@link #REQUEST_URI_ATTRIBUTE_NAME}.
     *
     * @param request request where to store the URI.
     * @param uri request URI.
     */
    void setRequestUriAttribute(ServletRequest request, String uri) {
        request.setAttribute(REQUEST_URI_ATTRIBUTE_NAME, uri)
    }
}
