package org.grails.plugins.jaxrs.swagger

import grails.core.GrailsApplication
import org.grails.config.NavigableMap

abstract class SwaggerInitializationUtil {
    /**
     * Retrieves, validates, and returns the swagger configuration.
     *
     * @return Validated swagger configuration.
     */
    static NavigableMap getSwaggerConfiguration(GrailsApplication grailsApplication) {
        NavigableMap data = grailsApplication.config.org.grails.jaxrs.swagger

        if (!data) {
            throw new IllegalStateException("the swagger configuration is missing")
        }

        if (!valueFor(String, data.resourcePackage)) {
            throw new IllegalStateException("the swagger configuration requires a resourcePackage path")
        }

        return data
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
    static Class<?> classFor(def fqcn, Class<?> fallback) throws ClassNotFoundException {
        fqcn = valueFor(String, fqcn)

        if (fqcn) {
            return getClass().getClassLoader().loadClass(fqcn)
        }

        return fallback
    }

    /**
     * Returns the given value iff it is the given type and not null.
     * Otherwise, the fallback value is returned.
     *
     * @param type
     * @param value
     * @param fallback
     * @return
     */
    static public <T> T valueFor(Class<T> type, def value, T fallback = null) {
        if (value == null) {
            return fallback
        }

        if (type.isInstance(value)) {
            return (T) value
        }

        return fallback
    }

    /**
     * Returns whether the plugin is enabled.
     *
     * @return
     */
    static boolean isSwaggerEnabled(GrailsApplication grailsApplication) {
        return valueFor(Boolean, getSwaggerConfiguration(grailsApplication).enabled, true)
    }
}
