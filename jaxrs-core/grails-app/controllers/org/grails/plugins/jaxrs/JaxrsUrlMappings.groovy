package org.grails.plugins.jaxrs
/*
 * Copyright 2009, 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.grails.plugins.jaxrs.core.JaxrsUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

/**
 * Defined URL mapping for the JaxrsController. The JAX-RS controller is responsible
 * for receiving Grails HTTP requests and manually passing those requests off to
 * the JAX-RS servlet. These mappings are generated for the root paths of all
 * registered resources so that their requests and be handled appropriately.
 *
 * For example, with a resource with a root path of "/test" and another with the
 * root path of "/notes", the following routes are created to the JAX-RS controller:
 *
 * <ul>
 * <li><code>/test</code></li>
 * <li><code>/test/**</code></li>
 * <li><code>/notes</code></li>
 * <li><code>/notes/**</code></li>
 * <ul>
 *
 * @author Martin Krasser
 * @author Bud Byrd
 */
class JaxrsUrlMappings {
    /**
     * Create URL mappings for all resources.
     */
    static mappings = { ApplicationContext applicationContext ->
        Logger logger = LoggerFactory.getLogger(JaxrsUrlMappings)

        logger.debug('URL mappings for JaxrsController:')
        JaxrsUtil.getInstance(applicationContext).retrieveRootPathList().each { pattern ->
            "${pattern}"(controller: "jaxrs")
            "${pattern}/**"(controller: "jaxrs")

            logger.debug("    ${pattern}")
            logger.debug("    ${pattern}/**")
        }
        "/application.wadl"(controller: "jaxrs")
    }
}

