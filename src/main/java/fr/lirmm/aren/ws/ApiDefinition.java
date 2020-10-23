package fr.lirmm.aren.ws;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

/**
 *
 * @author florent
 */
@OpenAPIDefinition(
        info = @Info(
                title = "AREN API",
                description = "API for the ARgumentation Et Numérique project",
                version = "3.8.0",
                license = @License(name = "MIT License", url = "https://github.com/aren-consortium/AREN/blob/master/LICENSE")
        ),
        security = {
            @SecurityRequirement(name = "CookieAuth"),
            @SecurityRequirement(name = "BearerAuth"),
            @SecurityRequirement(name = "QueryAuth")
        }
)
@SecuritySchemes({
    @SecurityScheme(
            name = "CookieAuth",
            type = SecuritySchemeType.APIKEY,
            in = SecuritySchemeIn.COOKIE,
            paramName = "Authorization"
    ),
    @SecurityScheme(
            name = "BearerAuth",
            type = SecuritySchemeType.APIKEY,
            in = SecuritySchemeIn.HEADER,
            paramName = "Authorization",
            description = "Bearer {key}"
    ),
    @SecurityScheme(
            name = "QueryAuth",
            type = SecuritySchemeType.APIKEY,
            in = SecuritySchemeIn.QUERY,
            paramName = "token"
    )}
)
public interface ApiDefinition {

}
