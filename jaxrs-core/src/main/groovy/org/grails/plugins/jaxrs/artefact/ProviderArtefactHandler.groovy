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
package org.grails.plugins.jaxrs.artefact

import grails.core.ArtefactHandlerAdapter
import groovy.util.logging.Slf4j

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.MessageBodyReader
import javax.ws.rs.ext.MessageBodyWriter

/**
 * @author Martin Krasser
 * @author Bud Byrd
 */
@Slf4j
class ProviderArtefactHandler extends ArtefactHandlerAdapter {
    /**
     * The type of the artefact.
     */
    static final String TYPE = "Provider"

    /**
     * Name of the plugin that owns the artefact.
     */
    static final String PLUGIN_NAME = 'jaxrs-core'

    /**
     * Constructor.
     */
    ProviderArtefactHandler() {
        super(TYPE, GrailsProviderClass.class, DefaultGrailsProviderClass.class, TYPE)
    }

    /**
     * Returns <code>true</code> if the <code>clazz</code> either implements {@link MessageBodyReader},
     * {@link MessageBodyWriter} or {@link ExceptionMapper}.
     *
     * @param clazz The type of the class.
     * @return <code>true</code> if the class is a JAX-RS provider.
     */
    @Override
    boolean isArtefactClass(Class clazz) {
        if (clazz == null) {
            return false
        }

        boolean match = JaxrsClassHelper.isJaxrsProvider(clazz)
        if (match) {
            log.info("Detected JAX-RS provider: ${clazz.getName()}")
        }
        return match
    }

    /**
     * Returns the name of the plugin that owns the artefact.
     *
     * @return Name of the plugin that owns the artefact.
     */
    @Override
    String getPluginName() {
        return PLUGIN_NAME
    }
}
