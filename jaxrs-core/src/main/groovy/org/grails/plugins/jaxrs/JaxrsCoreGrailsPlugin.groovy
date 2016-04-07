package org.grails.plugins.jaxrs

import grails.core.GrailsApplication
import grails.plugins.Plugin
import org.grails.plugins.jaxrs.artefact.ProviderArtefactHandler
import org.grails.plugins.jaxrs.artefact.ResourceArtefactHandler
import org.grails.plugins.jaxrs.core.JaxrsContext
import org.grails.plugins.jaxrs.core.JaxrsFilter
import org.grails.plugins.jaxrs.core.JaxrsListener
import org.grails.plugins.jaxrs.core.JaxrsUtil
import org.grails.plugins.jaxrs.provider.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean
import org.springframework.core.Ordered

class JaxrsCoreGrailsPlugin extends Plugin {
    /**
     * Logger.
     */
    Logger log = LoggerFactory.getLogger(JaxrsCoreGrailsPlugin)

    /**
     * What version of Grails this plugin is intended for.
     */
    def grailsVersion = "3.0 > *"

    /**
     * Load order.
     */
    def loadAfter = ['hibernate3', 'hibernate4', 'hibernate5', 'controllers', 'services', 'spring-security-core']

    /**
     * Which files to watch for reloading.
     */
    def watchedResources = [
        "file:./grails-app/resources/**/*Resource.groovy",
        "file:./grails-app/providers/**/*Reader.groovy",
        "file:./grails-app/providers/**/*Writer.groovy",
        "file:./plugins/*/grails-app/resources/**/*Resource.groovy",
        "file:./plugins/*/grails-app/providers/**/*Reader.groovy",
        "file:./plugins/*/grails-app/providers/**/*Writer.groovy"
    ]

    /**
     * Plugin author.
     */
    def author = "Bud Byrd"

    /**
     * Author email address.
     */
    def authorEmail = "bud.byrd@gmail.com"

    /**
     * Plugin title.
     */
    def title = "JAX-RS Plugin"

    /**
     * Description of the plugin.
     */
    def description = """
A plugin that supports the development of RESTful web services based on the
Java API for RESTful Web Services (JAX-RS). It is targeted atdevelopers who
want to structure the web service layer of an application in a JAX-RS
compatible way but still want to continue to use Grails' powerful features
such as GORM, automated XML and JSON marshalling, Grails services, Grails
filters and so on. This plugin is an alternative to Grails' built-in
mechanism for implementing  RESTful web services.
"""

    /**
     * Developers who have contributed to the development of the plugin.
     */
    def developers = [
        [name: 'Davide Cavestro', email: 'davide.cavestro@gmail.com'],
        [name: 'Noam Y. Tenne', email: 'noam@10ne.org'],
        [name: 'Martin Krasser', email: 'krasserm@googlemail.com']
    ]

    /**
     * Documentation URL.
     */
    def documentation = [url: 'https://budjb.github.io/grails-jaxrs/3.x/latest/']

    /**
     * Issues URL.
     */
    def issueManagement = [url: 'https://github.com/budjb/grails-jaxrs/issues']

    /**
     * Source control URL.
     */
    def scm = [url: 'https://github.com/budjb/grails-jaxrs']

    /**
     * Adds the JaxrsContext and plugin- and application-specific JAX-RS
     * resource and provider classes to the application context.
     */
    Closure doWithSpring() {{ ->
        jaxrsListener(ServletListenerRegistrationBean) {
            listener = bean(JaxrsListener)
            order = Ordered.LOWEST_PRECEDENCE
        }

        jaxrsFilter(FilterRegistrationBean) {
            filter = bean(JaxrsFilter)
            order = Ordered.HIGHEST_PRECEDENCE + 10
        }

        jaxrsContext(JaxrsContext) {
            jaxrsServletFactory = ref('jaxrsServletFactory')
        }

        "${JaxrsUtil.BEAN_NAME}"(JaxrsUtil) { bean ->
            bean.autowire = true
        }

        "${XMLWriter.name}"(XMLWriter)
        "${XMLReader.name}"(XMLReader)
        "${JSONWriter.name}"(JSONWriter)
        "${JSONReader.name}"(JSONReader)
        "${DomainObjectReader.name}"(DomainObjectReader)
        "${DomainObjectWriter.name}"(DomainObjectWriter)

        String requestedScope = getResourceScope(grailsApplication)

        grailsApplication.resourceClasses.each { rc ->
            "${rc.propertyName}"(rc.clazz) { bean ->
                bean.scope = requestedScope
                bean.autowire = true
            }
        }

        grailsApplication.providerClasses.each { pc ->
            "${pc.propertyName}"(pc.clazz) { bean ->
                bean.scope = 'singleton'
                bean.autowire = true
            }
        }
    }}

    /**
     * Reconfigures the JaxrsApplicationConfig with plugin- and application-specific
     * JAX-RS resource and provider classes.
     */
    void doWithApplicationContext() {
        if (!isEnabled(grailsApplication)) {
            log.info "Not starting JAX-RS servlet due to application configuration."
            return
        }

        JaxrsUtil jaxrsUtil = JaxrsUtil.getInstance(applicationContext)
        jaxrsUtil.setupJaxrsContext()
        jaxrsUtil.jaxrsContext.init()
    }

    /**
     * Updates application-specific JAX-RS resource and provider classes in
     * the application context.
     */
    void onChange(Map<String, Object> event) {
        if (!event.ctx) {
            return
        }

        // Determine the requested resource bean scope
        String requestedScope = getResourceScope(grailsApplication)

        if (grailsApplication.isArtefactOfType(ResourceArtefactHandler.TYPE, event.source as Class)) {
            def resourceClass = grailsApplication.addArtefact(ResourceArtefactHandler.TYPE, event.source as Class)
            beans {
                "${resourceClass.propertyName}"(resourceClass.clazz) { bean ->
                    bean.scope = requestedScope
                    bean.autowire = true
                }
            }.registerBeans(event.ctx)
        }
        else if (grailsApplication.isArtefactOfType(ProviderArtefactHandler.TYPE, event.source as Class)) {
            def providerClass = grailsApplication.addArtefact(ProviderArtefactHandler.TYPE, event.source as Class)
            beans {
                "${providerClass.propertyName}"(providerClass.clazz) { bean ->
                    bean.scope = 'singleton'
                    bean.autowire = true
                }
            }.registerBeans(event.ctx)
        }
        else {
            return
        }

        if (!isEnabled(grailsApplication)) {
            log.info "Not restarting JAX-RS servlet due to application configuration."
        }
        else {
            JaxrsUtil.getInstance().setupJaxrsContext()
            JaxrsUtil.getInstance().jaxrsContext.restart()
        }
    }

    /**
     * Returns the scope for all resource classes as requested by
     * the application configuration. Defaults to "prototype".
     *
     * @param application
     * @return
     */
    private String getResourceScope(GrailsApplication application) {
        def scope = application.config.org.grails.jaxrs.resource.scope
        if (!scope) {
            scope = 'prototype'
        }
        return scope
    }

    /**
     * Returns whether the plugin is enabled.
     *
     * @param application
     * @return
     */
    private boolean isEnabled(GrailsApplication application) {
        if (application.config.org.grails.jaxrs.enabled == false) {
            return false
        }
        return true
    }
}
