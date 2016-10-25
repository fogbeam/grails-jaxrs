package org.grails.plugins.jaxrs.swagger

import grails.plugins.Plugin
import grails.web.mapping.LinkGenerator
import io.swagger.jaxrs.config.BeanConfig
import org.grails.plugins.jaxrs.core.ScanningResourceRegistrar

class JaxrsSwaggerGrailsPlugin extends Plugin {
    /**
     * The version or versions of Grails the plugin is designed for.
     */
    def grailsVersion = "3.0 > *"

    /**
     * Headline display name of the plugin.
     */
    def title = "Swagger Support for JAX-RS"

    /**
     * Plugin author.
     */
    def author = "Bud Byrd"

    /**
     * Author email address.
     */
    def authorEmail = "bud.byrd@gmail.com"

    /**
     * Plugin description.
     */
    def description = 'Adds Swagger support to the JAX-RS plugin. This plugin only includes' +
        'the functionality to generate the swagger data, and does not include swagger-ui.'

    /**
     * URL to the plugin's documentation.
     */
    def documentation = "https://budjb.github.io/grails-jaxrs/3.x/latest/"

    /**
     * Plugin license.
     */
    def license = "APACHE"

    /**
     * Additional developers.
     */
    def developers = [
        [name: 'Donald Jackson', email: 'donald@ddj.co.za'],
        [name: "Angel Ruiz", email: "aruizca@gmail.com"],
        [name: "Aaron Brown", email: "brown.aaron.lloyd@gmail.com"],
        [name: "nerdErg Pty. Ltd.", url: "http://www.nerderg.com"]
    ]

    /**
     * Issues URL.
     */
    def issueManagement = [url: 'https://github.com/budjb/grails-jaxrs/issues']

    /**
     * Source control URL.
     */
    def scm = [url: 'https://github.com/budjb/grails-jaxrs']

    /**
     * Configure Spring beans.
     */
    Closure doWithSpring() {
        { ->
            Map config = getSwaggerConfiguration()

            String baseUrl = getBaseUrl(config)

            if (isPluginEnabled(config)) {
                'swaggerConfig'(classFor(config.beanConfigClassName, BeanConfig)) { bean ->
                    bean.autowire = true
                    resourcePackage = config.resourcePackage
                    version = config.version ?: '1'
                    title = config.title ?: 'Unspecified'
                    description = config.description ?: ''
                    contact = config.contact ?: ''
                    license = config.license ?: ''
                    licenseUrl = config.licenseUrl ?: ''
                    scan = config.scan ?: true

                    if (baseUrl) {
                        URI uri = new URI(baseUrl)

                        if (uri.port != -1) {
                            host = "${uri.host}:${uri.port}"
                        }
                        else {
                            host = uri.host
                        }
                        schemes = [uri.scheme]
                        basePath = uri.path
                    }
                }
                'swaggerResourceRegistrar'(ScanningResourceRegistrar, 'io.swagger.jaxrs.listing')
            }
        }
    }

    /**
     * Retrieves, validates, and returns the swagger configuration.
     *
     * @return Validated swagger configuration.
     */
    Map getSwaggerConfiguration() {
        Map config = getGrailsApplication().config.org.grails.jaxrs.swagger as Map

        if (!config) {
            throw new IllegalStateException("the swagger configuration is missing")
        }

        if (!config.resourcePackage || !(config.resourcePackage instanceof String)) {
            throw new IllegalStateException("the swagger configuration requires a resourcePackage path")
        }

        return config
    }

    /**
     * Returns the class type with the given name, if the name is a string.
     *
     * If the name is not a string or it is empty, the fallback class is returned.
     *
     * @param fqcn
     * @param fallback
     * @return
     */
    Class<?> classFor(def fqcn, Class<?> fallback) throws ClassNotFoundException {
        if (fqcn && fqcn instanceof String) {
            return getClass().getClassLoader().loadClass(fqcn)
        }

        return fallback
    }

    /**
     * Returns whether the plugin is enabled.
     *
     * @return
     */
    boolean isPluginEnabled(Map configuration) {
        def enabled = configuration.enabled

        return !(enabled instanceof Boolean) || enabled
    }

    /**
     * Returns the configured base URL, or null if one isn't set.
     *
     * Attempts to try <pre>grails.plugins.jaxrs.swagger.baseUrl</pre> first, and then
     * attempts to try <pre>grails.serverURL</pre>.
     *
     * @param config
     * @return
     */
    String getBaseUrl(Map config) {
        String baseUrl = config.baseUrl ?: getConfig().grails.serverURL ?: null

        if (baseUrl && baseUrl instanceof String) {
            return baseUrl
        }

        return null
    }
}
