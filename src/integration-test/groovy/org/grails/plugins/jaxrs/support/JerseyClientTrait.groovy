package org.grails.plugins.jaxrs.support

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult

trait JerseyClientTrait {
    ResponseProperties makeGetRequest(RequestProperties requestProperties) {
        return createResponse(createRequestBuilder(requestProperties).get(ClientResponse))
    }

    ResponseProperties makePostRequest(RequestProperties requestProperties) {
        WebResource.Builder builder = createRequestBuilder(requestProperties)

        if (requestProperties.body != null) {
            return createResponse(builder.post(ClientResponse, requestProperties.body))
        }
        else {
            return createResponse(builder.post(ClientResponse))
        }
    }

    private ResponseProperties createResponse(ClientResponse response) {
        ResponseProperties properties = new ResponseProperties(
            status: response.status,
            contentType: response.type.toString(),
            body: response.getEntity(String)
        )

        return properties
    }

    private WebResource.Builder createRequestBuilder(RequestProperties requestProperties) {
        ClientConfig config = new DefaultClientConfig()

        Client client = Client.create(config)

        WebResource webResource = client.resource(requestProperties.uri)

        WebResource.Builder builder = webResource.getRequestBuilder()

        if (requestProperties.accept) {
            builder = builder.accept(requestProperties.accept)
        }

        if (requestProperties.contentType) {
            builder = builder.type(requestProperties.contentType)
        }

        if (requestProperties.headers) {
            requestProperties.headers.each { key, value ->
                builder = builder.header(key, value)
            }
        }

        return builder
    }

    static class RequestProperties {
        String uri
        def body
        String contentType
        String accept
        Map<String, String> headers = [:]
    }

    static class ResponseProperties {
        int status
        String body
        String contentType
    }

    def parseJson(String raw) {
        return new JsonSlurper().parseText(raw)
    }

    GPathResult parseXml(String raw) {
        return new XmlSlurper().parseText(raw)
    }
}
