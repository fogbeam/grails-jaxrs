package org.grails.plugins.jaxrs.restlet

import grails.plugins.Plugin

class JaxrsRestletGrailsPlugin extends Plugin {
    /**
     * Version of Grails the plugin is meant for.
     */
    def grailsVersion = "3.0 > *"

    /**
     * Plugin title.
     */
    def title = "JAX-RS Restlet Implementation Plugin"

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
    def description = 'A plugin that provides the Restlet implement of the JAX-RS plugin.'

    /**
     * URL to the plugin's documentation.
     */
    def documentation = 'https://budjb.github.io/grails-jaxrs/3.x/latest/'

    /**
     * Project license.
     */
    def license = "APACHE"

    /**
     * Location of the plugin's issue tracker.
     */
    def issueManagement = [url: 'https://github.com/budjb/grails-jaxrs/issues']

    /**
     * Online location of the plugin's browseable source code.
     */
    def scm = [url: 'https://github.com/budjb/grails-jaxrs']

    /**
     * Register Spring beans.
     *
     * @return
     */
    Closure doWithSpring() {{ ->
        jaxrsServletFactory(RestletServletFactory) { bean ->
            bean.autowire = true
        }
    }}
}
