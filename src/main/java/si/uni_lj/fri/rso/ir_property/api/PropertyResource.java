package si.uni_lj.fri.rso.ir_property.api;

import si.uni_lj.fri.rso.ir_property.cdi.PropertyDatabase;
import si.uni_lj.fri.rso.ir_property.models.Property;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("properties")
public class PropertyResource {
    @GET
    public Response getAllProperties() {
        List<Property> properties = PropertyDatabase.getProperties();
        return Response.ok(properties).build();
    }

    @GET
    @Path("/{propertyId}")
    public Response getProperty(@PathParam("propertyId") String propertyId) {
        Property property = PropertyDatabase.getProperty(propertyId);
        return property != null
                ? Response.ok(property).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewProperty(Property property) {
        PropertyDatabase.addProperty(property);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{propertyId}")
    public Response deleteProperty(@PathParam("propertyId") String propertyId) {
        PropertyDatabase.deleteProperty(propertyId);
        return Response.noContent().build();
    }
}
