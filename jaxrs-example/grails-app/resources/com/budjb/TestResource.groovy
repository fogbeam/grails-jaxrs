package com.budjb

import io.swagger.annotations.Api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path('/api/test')
@Api('test')
class TestResource {
    @GET
    @Produces('text/plain')
    String getTestRepresentation() {
        'Test'
    }
}