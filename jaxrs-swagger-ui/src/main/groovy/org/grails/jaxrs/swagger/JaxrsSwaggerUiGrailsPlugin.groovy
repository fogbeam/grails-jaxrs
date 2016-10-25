package org.grails.jaxrs.swagger

import grails.plugins.*

class JaxrsSwaggerUiGrailsPlugin extends Plugin {
    /**
     * The version or versions of Grails the plugin is designed for.
     */
    def grailsVersion = "3.0 > *"

    /**
     * Headline display name of the plugin.
     */
    def title = "Swagger UI Integration for JAX-RS"

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
    def description = 'Adds Swagger support to the JAX-RS plugin.'

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
}
