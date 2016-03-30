package ${packageName}

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path('/api/${resourcePath}')
class ${resourceName}Resource {
    @GET
    @Produces('text/plain')
    String get${resourceName}Representation() {
        '${resourceName}'
    }
}