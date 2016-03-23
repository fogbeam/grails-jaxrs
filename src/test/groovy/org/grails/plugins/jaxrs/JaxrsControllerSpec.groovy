/*
 * Copyright 2009 - 2011 the original author or authors.
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

import grails.test.mixin.TestFor
import org.grails.plugins.jaxrs.web.JaxrsContext
import org.grails.plugins.jaxrs.web.JaxrsUtils
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Martin Krasser
 * @author Bud Byrd
 */
@TestFor(JaxrsController)
class JaxrsControllerSpec extends Specification {
    JaxrsContext jaxrsContext
    HttpServlet httpServlet

    def setup() {
        httpServlet = new HttpServlet() {
            void service(HttpServletRequest request, HttpServletResponse response) {
                MockHttpServletResponse mockResponse = (MockHttpServletResponse) response
                mockResponse.writer.println(request.requestURI)
                mockResponse.writer.flush()
            }

            void init(ServletConfig config) {
                // nothing to do
            }
        }

        jaxrsContext = new JaxrsContext()
        jaxrsContext.init(httpServlet)

        controller.jaxrsContext = jaxrsContext
    }

    def 'Ensure requests get handed off correctly'() {
        setup:
        controller.request.method = 'GET'
        JaxrsUtils.setRequestUriAttribute(controller.request, '/test')

        when:
        controller.handle()

        then:
        controller.response.contentAsString.trim() == '/test'
    }
}

