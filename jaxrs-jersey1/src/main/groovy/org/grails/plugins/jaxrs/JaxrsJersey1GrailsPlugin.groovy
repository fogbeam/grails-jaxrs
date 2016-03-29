package org.grails.plugins.jaxrs

import grails.plugins.Plugin
import org.grails.plugins.jaxrs.jersey.JerseyServletFactory

class JaxrsJersey1GrailsPlugin extends Plugin {
    /**
     * Version of Grails the plugin is meant for.
     */
    def grailsVersion = "3.1.4 > *"

    /**
     * Plugin title.
     */
    def title = "Jersey 1.x implementation of the JAX-RS Grails plugin."

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
    def description = 'TODO'

    /**
     * URL to the plugin's documentation.
     */
    def documentation = "TODO"

    /**
     * Project license.
     */
    def license = "APACHE"

    /**
     * Location of the plugin's issue tracker.
     */
    def issueManagement = "https://github.com/budjb/grails-jaxrs/issues"

    /**
     * Online location of the plugin's browseable source code.
     */
    def scm = "https://github.com/budjb/grails-jaxrs"

    /**
     * Register Spring beans.
     *
     * @return
     */
    Closure doWithSpring() {
        { ->
            'jaxrsServletFactory'(JerseyServletFactory) { bean ->
                bean.autowire = true
            }
        }
    }
}
