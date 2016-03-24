package org.grails.plugins.jaxrs

import grails.plugins.Plugin
import org.grails.plugins.jaxrs.generator.CodeGenerator
import org.grails.plugins.jaxrs.provider.*
import org.grails.plugins.jaxrs.web.JaxrsContext
import org.grails.plugins.jaxrs.web.JaxrsFilter
import org.grails.plugins.jaxrs.web.JaxrsListener
import org.grails.plugins.jaxrs.web.JaxrsUtils
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean
import org.springframework.core.Ordered

import static org.grails.plugins.jaxrs.web.JaxrsUtils.JAXRS_CONTEXT_NAME

class JaxrsGrailsPlugin extends Plugin {
    def version = "0.12"
    def grailsVersion = "3.0 > *"
    def pluginExcludes = [
            "grails-app/domain/*",
            "grails-app/providers/*",
            "grails-app/resources/*",
            "src/groovy/org/grails/jaxrs/test/*",
            "lib/*-sources.jar",
            "web-app/**"
    ]

    def loadAfter = ['controllers', 'services', 'spring-security-core']

    def watchedResources = [
            "file:./grails-app/resources/**/*Resource.groovy",
            "file:./grails-app/providers/**/*Reader.groovy",
            "file:./grails-app/providers/**/*Writer.groovy",
            "file:./plugins/*/grails-app/resources/**/*Resource.groovy",
            "file:./plugins/*/grails-app/providers/**/*Reader.groovy",
            "file:./plugins/*/grails-app/providers/**/*Writer.groovy"
    ]

    def author = "Martin Krasser"
    def authorEmail = "krasserm@googlemail.com"
    def title = "JSR 311 plugin"
    def description = """
A plugin that supports the development of RESTful web services based on the
Java API for RESTful Web Services (JSR 311: JAX-RS). It is targeted at
developers who want to structure the web service layer of an application in
a JSR 311 compatible way but still want to continue to use Grails' powerful
features such as GORM, automated XML and JSON marshalling, Grails services,
Grails filters and so on. This plugin is an alternative to Grails' built-in
mechanism for implementing  RESTful web services.

At the moment, plugin users may choose between Jersey and Restlet as JAX-RS
implementation. Both implementations are packaged with the plugin. Support for
Restlet was added in version 0.2 of the plugin in order to support deployments
on the Google App Engine. Other JAX-RS implementations such as RestEasy or
Apache Wink are likely to be added in upcoming versions of the plugin.
"""

    def developers = [
            [name: 'Davide Cavestro', email: 'davide.cavestro@gmail.com'],
            [name: 'Noam Y. Tenne', email: 'noam@10ne.org'],
            [name: 'Bud Byrd', email: 'bud.byrd@gmail.com']
    ]

    def documentation = 'https://github.com/krasserm/grails-jaxrs/wiki'

    def issueManagement = [url: 'https://github.com/krasserm/grails-jaxrs/issues']

    def scm = [url: 'https://github.com/krasserm/grails-jaxrs']

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
            order = Ordered.HIGHEST_PRECEDENCE+10
        }

        // Configure the JAX-RS context
        jaxrsContext(JaxrsContext)

        // Configure default providers
        "${XMLWriter.name}"(XMLWriter)
        "${XMLReader.name}"(XMLReader)
        "${JSONWriter.name}"(JSONWriter)
        "${JSONReader.name}"(JSONReader)
        "${DomainObjectReader.name}"(DomainObjectReader)
        "${DomainObjectWriter.name}"(DomainObjectWriter)

        // Determine the requested resource bean scope
        String requestedScope = getResourceScope(grailsApplication)

        // Configure application-provided resources
        grailsApplication.resourceClasses.each { rc ->
            "${rc.propertyName}"(rc.clazz) { bean ->
                bean.scope = requestedScope
                bean.autowire = true
            }
        }

        // Configure application-provided providers
        grailsApplication.providerClasses.each { pc ->
            "${pc.propertyName}"(pc.clazz) { bean ->
                bean.scope = 'singleton'
                bean.autowire = true
            }
        }

        // Configure the resource code generator
        "${CodeGenerator.name}"(CodeGenerator)
    }}

    /**
     * Updates application-specific JAX-RS resource and provider classes in
     * the application context.
     */
    void onChange(event) {
        if (!event.ctx) {
            return
        }

        // Determine the requested resource bean scope
        String requestedScope = getResourceScope(grailsApplication)

        if (grailsApplication.isArtefactOfType(ResourceArtefactHandler.TYPE, event.source)) {
            def resourceClass = grailsApplication.addArtefact(ResourceArtefactHandler.TYPE, event.source)
            beans {
                "${resourceClass.propertyName}"(resourceClass.clazz) { bean ->
                    bean.scope = requestedScope
                    bean.autowire = true
                }
            }.registerBeans(event.ctx)
        } else if (grailsApplication.isArtefactOfType(ProviderArtefactHandler.TYPE, event.source)) {
            def providerClass = grailsApplication.addArtefact(ProviderArtefactHandler.TYPE, event.source)
            beans {
                "${providerClass.propertyName}"(providerClass.clazz) { bean ->
                    bean.scope = 'singleton'
                    bean.autowire = true
                }
            }.registerBeans(event.ctx)
        } else {
            return
        }

        // Setup the JaxrsConfig
        JaxrsContext context = applicationContext.getBean(JAXRS_CONTEXT_NAME, JaxrsContext)
        JaxrsUtils.setupJaxrsContext(context, grailsApplication)
        context.refresh();
    }

    /**
     * Reconfigures the JaxrsConfig with plugin- and application-specific
     * JAX-RS resource and provider classes. Configures the JaxrsContext
     * with the JAX-RS implementation to use. The name of the JAX-RS
     * implementation is obtained from the configuration property
     * <code>org.grails.jaxrs.provider.name</code>. Default value is
     * <code>jersey</code>.
     */
    void doWithApplicationContext() {
        JaxrsContext context = applicationContext.getBean(JAXRS_CONTEXT_NAME, JaxrsContext)
        JaxrsUtils.setupJaxrsContext(context, grailsApplication)
        context.init();
    }

    /**
     * Returns the scope for all resource classes as requested by
     * the application configuration. Defaults to "prototype".
     *
     * @param application
     * @return
     */
    private String getResourceScope(application) {
        def scope = application.config.org.grails.jaxrs.resource.scope
        if (!scope) {
            scope = 'prototype'
        }
        return scope
    }
}
