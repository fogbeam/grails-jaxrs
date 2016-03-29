/*
 * Copyright 2009 the original author or authors.
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
package org.grails.plugins.jaxrs

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.grails.plugins.jaxrs.artefact.JaxrsClasses
import org.grails.plugins.jaxrs.test.*
import spock.lang.Specification

/**
 * @author Martin Krasser
 * @author Bud Byrd
 */
@TestMixin(GrailsUnitTestMixin)
class JaxrsClassesSpec extends Specification {
    def 'Ensure resources are correctly identified as resources'() {
        expect:
        JaxrsClasses.isJaxrsResource(TestA)
        JaxrsClasses.isJaxrsResource(TestB)
        JaxrsClasses.isJaxrsResource(TestC)
    }

    def 'Ensure that objects that are not resources are not identified as resources'() {
        expect:
        !JaxrsClasses.isJaxrsResource(TestD)
        !JaxrsClasses.isJaxrsResource(TestE)
    }

    def 'Ensure that resources whose parents are resources are correctly identified as resources'() {
        expect:
        JaxrsClasses.isJaxrsResource(TestH1B)
        !JaxrsClasses.isJaxrsResource(TestH2B)
        JaxrsClasses.isJaxrsResource(TestH3B)
    }
}
