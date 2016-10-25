package org.grails.jaxrs.swagger

import io.swagger.jaxrs.config.BeanConfig

class ShowRestApiController {
    BeanConfig swaggerConfig

    def index() {
        swaggerConfig.basePath = request.contextPath
        render(view: 'index', model: [apiDocsPath: "${request.contextPath}/api-docs"])
    }
}
