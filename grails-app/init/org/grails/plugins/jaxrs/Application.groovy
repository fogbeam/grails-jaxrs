package org.grails.plugins.jaxrs

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.grails.plugins.jaxrs.web.JaxrsListener
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Bean
    public ServletListenerRegistrationBean<JaxrsListener> jaxrsListenerRegister() {
        return new ServletListenerRegistrationBean<JaxrsListener>(new JaxrsListener())
    }
}
