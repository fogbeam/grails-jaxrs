package org.grails.plugins.jaxrs.servlet.restlet

import org.restlet.ext.jaxrs.InstantiateException
import org.restlet.ext.jaxrs.ObjectFactory
import org.springframework.context.ApplicationContext

import javax.servlet.ServletContext

import static org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext

/**
 * An object factory that looks up a resource or provider class from the Spring
 * application context based on its class type.
 *
 * @author Bud Byrd
 */
class ApplicationContextObjectFactory implements ObjectFactory {
    /**
     * Application context.
     */
    ApplicationContext applicationContext

    /**
     * Constructor.
     *
     * @param servletContext
     */
    ApplicationContextObjectFactory(ServletContext servletContext) {
        this.applicationContext = getRequiredWebApplicationContext(servletContext)
    }

    /**
     * Returns the instance of the requested JAX-RS resource or provider from
     * the Spring application context.
     *
     * @param jaxRsClass
     * @return
     * @throws org.restlet.ext.jaxrs.InstantiateException
     */
    public <T> T getInstance(Class<T> jaxRsClass) throws InstantiateException {
        return (T) applicationContext.getBeansOfType(jaxRsClass).values().iterator().next()
    }
}
