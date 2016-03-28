package org.grails.plugins.jaxrs.servlet

import javax.servlet.Servlet

abstract class ServletLoader {
    /**
     * Create the servlet instance for the implementation-specific provider.
     *
     * @return
     */
    abstract Servlet createServlet()
}
