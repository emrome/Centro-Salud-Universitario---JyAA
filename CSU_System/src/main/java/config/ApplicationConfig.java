package config;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(
                title = "CSU System API",
                version = "1.0",
                description = "API para gestión del sistema CSU"
        ),
        servers = @Server(url = "/CSU_System"),
        tags = {
                @Tag(name = "Neighborhood Resource", description = "Handles operations related to neighborhoods"),
                @Tag(name = "Zone Resource", description = "Manages zones within neighborhoods and their geolocation"),
                @Tag(name = "Surveyor Resource", description = "Manages surveyor information"),
                @Tag(name = "Campaign Resource", description = "Handles operations related to campaigns"),
                @Tag(name = "Event Resource", description = "Handles operations related to events within campaigns"),
                @Tag(name = "Admin Resource", description = "Administrator management operations"),
                @Tag(name = "Health Staff Resource", description = "Manages healthcare professionals and their specialties"),
                @Tag(name = "SocialOrgRepresentative Resource", description = "Handles representatives of social organizations"),
                @Tag(name = "Auth Resource", description = "Handles user authentication and authorization"),
        }
)

public class ApplicationConfig extends Application {
    // This class is intentionally left empty.
    // It serves as a configuration class for JAX-RS in the application.
    // The @ApplicationPath annotation defines the base URI for all JAX-RS resources.
}
