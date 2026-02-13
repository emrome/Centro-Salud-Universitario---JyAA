package resources.auth;

import dtos.auth.LoginRequestDTO;
import dtos.auth.LoginResponseDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import services.auth.AuthService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    public Response login(LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return Response.ok(response).build();
    }
}



