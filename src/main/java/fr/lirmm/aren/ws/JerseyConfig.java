package fr.lirmm.aren.ws;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey configuration class.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationPath("ws")
public class JerseyConfig extends ResourceConfig {

    /**
     *
     */
    public JerseyConfig() {

        packages("fr.lirmm.aren.ws.rest");

        packages("fr.lirmm.aren.ws.filter");

        packages("fr.lirmm.aren.ws.exceptionmapper");

        register(ObjectMapperProvider.class);

        register(MultiPartFeature.class);

        register(AcceptHeaderOpenApiResource.class);
        register(OpenApiResource.class);
        register(SwaggerSerializers.class);
    }

}
