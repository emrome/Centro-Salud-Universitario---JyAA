package resources.people;

import dtos.people.AdminCreateDTO;
import dtos.people.AdminDTO;
import interceptors.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import services.people.AdminService;
import jakarta.ws.rs.core.SecurityContext;
import utils.SimpleMessage;

import java.net.URI;
import java.util.List;

@AdminOnly
@Path("/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Admin Resource")
public class AdminResource {
    @Context
    private SecurityContext securityContext;

    @Inject
    private AdminService service;

    @GET
    @Operation(
            summary = "List all admins",
            description = "Returns a list of all registered administrators",
            responses = @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    )
    public Response getAll() {
        List<AdminDTO> admins = service.getAll();
        return Response.ok(admins).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find admin by ID",
            description = "Returns the admin with the specified ID",
            parameters = @Parameter(name = "id", description = "ID of the admin"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin found"),
                    @ApiResponse(responseCode = "404", description = "Admin not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        AdminDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @POST
    @Operation(
            summary = "Create a new admin",
            description = "Registers a new administrator",
            requestBody = @RequestBody(
                    description = "Admin to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Admin example",
                                    summary = "Full administrator details",
                                    value = """
                                            {
                                              "firstName": "Carla",
                                              "lastName": "López",
                                              "birthDate": "1985-05-20",
                                              "email": "carla.lopez@csu.unlp.edu.ar",
                                              "password": "securePassword123",
                                              "registrationDate": "2025-06-01",
                                              "enabled": true,
                                              "positionInCSU": "General Coordinator"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Admin created successfully"),
                    @ApiResponse(responseCode = "409", description = "Admin with same email already exists")
            }
    )
    public Response create(AdminCreateDTO dto, @Context UriInfo uriInfo) {
        AdminDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update an admin",
            description = "Updates an existing administrator",
            parameters = @Parameter(name = "id", description = "ID of the admin to update"),
            requestBody = @RequestBody(
                    description = "Updated admin data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update admin example",
                                    summary = "Modified role and enablement status",
                                    value = """
                                        {
                                          "firstName": "Carla",
                                          "lastName": "López",
                                          "birthDate": "1980-09-12",
                                          "email": "carla.lopez@csu.unlp.edu.ar",
                                          "password": "newSecurePassword456",
                                          "registrationDate": "2025-06-01",
                                          "enabled": false,
                                          "positionInCSU": "Assistant Coordinator"
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Admin not found"),
                    @ApiResponse(responseCode = "409", description = "Another admin with same email already exists")
            }
    )
    public Response update(@PathParam("id") Long id, AdminDTO dto) {
        AdminDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete an admin",
            description = "Deletes an admin by ID",
            parameters = @Parameter(name = "id", description = "ID of the admin"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Admin deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: cannot delete yourself"),
                    @ApiResponse(responseCode = "404", description = "Admin not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        String sub = securityContext != null && securityContext.getUserPrincipal() != null
                ? securityContext.getUserPrincipal().getName()
                : null;

        if (sub != null) {
            AdminDTO target = service.getById(id); // lanza 404 si no existe
            boolean sameByNumericId = sub.equals(String.valueOf(id));
            boolean sameByEmail     = target.getEmail() != null && target.getEmail().equalsIgnoreCase(sub);

            if (sameByNumericId || sameByEmail) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new SimpleMessage("No podés eliminar tu propia cuenta de administrador."))
                        .build();
            }
        }

        service.delete(id);
        return Response.noContent().build();
    }
}
