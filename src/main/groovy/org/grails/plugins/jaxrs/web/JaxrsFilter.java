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
package org.grails.plugins.jaxrs.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet filter that stores the request URI as request attribute.
 *
 * @author Martin Krasser
 * @see RequestWrapper
 */
public class JaxrsFilter extends OncePerRequestFilter {

    /**
     * Stores the request URI as request attribute.
     *
     * @see JaxrsUtils#REQUEST_URI_ATTRIBUTE_NAME
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        JaxrsUtils.setRequestUriAttribute(request, request.getRequestURI());
        chain.doFilter(request, response);
    }

    @Bean
    public DispatcherServletBeanPostProcessor dispatcherServletBeanPostProcessor() {
        return new DispatcherServletBeanPostProcessor();
    }

    public static class DispatcherServletBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof DispatcherServlet) {
                //((DispatcherServlet) bean).setDispatchOptionsRequest(true);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}
