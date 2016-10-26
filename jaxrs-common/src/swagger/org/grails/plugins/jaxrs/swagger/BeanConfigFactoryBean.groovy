package org.grails.plugins.jaxrs.swagger

import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.util.Metadata
import io.swagger.jaxrs.config.BeanConfig
import org.grails.config.NavigableMap
import org.springframework.beans.factory.config.AbstractFactoryBean
import static SwaggerInitializationUtil.*

class BeanConfigFactoryBean extends AbstractFactoryBean<BeanConfig> implements GrailsApplicationAware {
    /**
     * Grails application bean.
     */
    GrailsApplication grailsApplication

    /**
     * Constructor.
     */
    BeanConfigFactoryBean() {
        setSingleton(true)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Class<?> getObjectType() {
        return BeanConfig.class
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanConfig createInstance() throws Exception {
        NavigableMap swaggerConfig = getSwaggerConfiguration(grailsApplication)

        Class<?> clazz = classFor(valueFor(String, swaggerConfig.beanConfigClassName), BeanConfig)
        BeanConfig beanConfig = (BeanConfig) clazz.newInstance()

        beanConfig.setResourcePackage(valueFor(String, swaggerConfig.resourcePackage))
        beanConfig.setVersion(valueFor(String, swaggerConfig.version, '1'))
        beanConfig.setTitle(valueFor(String, swaggerConfig.title, Metadata.current.getApplicationName()))
        beanConfig.setDescription(valueFor(String, swaggerConfig.description))
        beanConfig.setContact(valueFor(String, swaggerConfig.contact))
        beanConfig.setLicense(valueFor(String, swaggerConfig.license))
        beanConfig.setLicenseUrl(valueFor(String, swaggerConfig.licenseUrl))
        beanConfig.setScan(valueFor(Boolean, swaggerConfig.scan, true))

        String baseUrl = getBaseUrl(swaggerConfig)
        if (baseUrl) {
            URI uri = new URI(baseUrl)

            if (uri.port != -1) {
                beanConfig.setHost("${uri.host}:${uri.port}")
            }
            else {
                beanConfig.setHost(uri.host)
            }

            beanConfig.setSchemes([uri.scheme] as String[])
            beanConfig.setBasePath(uri.path)
        }

        return beanConfig
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
    String getBaseUrl(def baseUrl) {
        return valueFor(String, baseUrl, valueFor(String, grailsApplication.config.grails.serverURL))
    }
}
