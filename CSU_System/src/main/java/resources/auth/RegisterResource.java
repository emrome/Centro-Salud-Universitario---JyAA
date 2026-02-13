package resources.auth;
import dtos.auth.PublicRegistrationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.auth.RegisterService;

@Path("/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

    @Inject
    RegisterService registerService;

    @POST
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user in the system. The user type is determined by the provided data.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "409", description = "User already exists")
            }
    )
    public Response register(PublicRegistrationDTO dto) {
        registerService.register(dto);
        return Response.status(Response.Status.CREATED).build();
    }
}