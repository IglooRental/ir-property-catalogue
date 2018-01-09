package si.uni_lj.fri.rso.ir_property.api;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import org.eclipse.microprofile.metrics.annotation.Metered;
import si.uni_lj.fri.rso.ir_property.cdi.Config;
import si.uni_lj.fri.rso.ir_property.cdi.PropertyDatabase;
import si.uni_lj.fri.rso.ir_property.models.Property;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("properties")
@Log
public class PropertyResource {
    @Inject
    private PropertyDatabase propertyDatabase;

    @Context
    protected UriInfo uriInfo;

    private Logger log = Logger.getLogger(PropertyResource.class.getName());

    @GET
    @Metered
    public Response getAllProperties() {
        if (ConfigurationUtil.getInstance().getBoolean("rest-config.endpoint-enabled").orElse(false)) {
            List<Property> properties = propertyDatabase.getProperties();
            return Response.ok(properties).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"reason\": \"Endpoint disabled.\"}").build();
        }
    }

    @GET
    @Path("/filtered")
    public Response getPropertiesFiltered() {
        if (ConfigurationUtil.getInstance().getBoolean("rest-config.endpoint-enabled").orElse(false)) {
            List<Property> customers = propertyDatabase.getPropertiesFilter(uriInfo);
            return Response.status(Response.Status.OK).entity(customers).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"reason\": \"Endpoint disabled.\"}").build();
        }
    }

    @GET
    @Metered
    @Path("/{propertyId}")
    public Response getProperty(@PathParam("propertyId") String propertyId, @DefaultValue("true") @QueryParam("includeExtended") boolean includeExtended) {
        if (ConfigurationUtil.getInstance().getBoolean("rest-config.endpoint-enabled").orElse(false)) {
            Property property = propertyDatabase.getProperty(propertyId, includeExtended);
            return property != null
                    ? Response.ok(property).build()
                    : Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"reason\": \"Endpoint disabled.\"}").build();
        }
    }

    @POST
    @Metered
    public Response addNewProperty(Property property) {
        if (ConfigurationUtil.getInstance().getBoolean("rest-config.endpoint-enabled").orElse(false)) {
            propertyDatabase.createProperty(property);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"reason\": \"Endpoint disabled.\"}").build();
        }
    }

    @DELETE
    @Metered
    @Path("/{propertyId}")
    public Response deleteProperty(@PathParam("propertyId") String propertyId) {
        if (ConfigurationUtil.getInstance().getBoolean("rest-config.endpoint-enabled").orElse(false)) {
            propertyDatabase.deleteProperty(propertyId);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"reason\": \"Endpoint disabled.\"}").build();
        }
    }
}
