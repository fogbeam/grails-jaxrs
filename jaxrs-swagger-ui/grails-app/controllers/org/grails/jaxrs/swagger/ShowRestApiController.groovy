package org.grails.jaxrs.swagger

import io.swagger.jaxrs.config.BeanConfig
import org.springframework.beans.factory.InitializingBean

class ShowRestApiController implements InitializingBean {
    BeanConfig swaggerConfig

    /**
     * {@inheritDoc}
     */
    @Override
    void afterPropertiesSet() throws Exception {
        assert swaggerConfig != null, "swagger must be supported and enabled for swagger-ui to function"
    }

    def index() {
        if (swaggerConfig.basePath == null && request.contextPath) {
            swaggerConfig.setBasePath(request.contextPath)
        }
        render(view: 'index', model: [apiDocsPath: "${request.contextPath}/api-docs"])
    }
}
