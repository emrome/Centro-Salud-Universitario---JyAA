package resources.people;

import dtos.people.HealthStaffCreateDTO;
import dtos.people.HealthStaffDTO;
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
import services.people.HealthStaffService;

import java.net.URI;
import java.util.List;

@Path("/healthstaff")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health Staff Resource")
public class HealthStaffResource {

    @Inject
    private HealthStaffService service;

    @GET
    @AdminOnly
    @Operation(
            summary = "List all health staff members",
            description = "Returns a list of all health staff users",
            responses = @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    )
    public Response getAll() {
        List<HealthStaffDTO> list = service.getAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find health staff by ID",
            description = "Returns the health staff user with the specified ID",
            parameters = @Parameter(name = "id", description = "ID of the health staff user"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        HealthStaffDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @POST
    @Operation(
            summary = "Create a new health staff user",
            description = "Registers a new health staff user in the system",
            requestBody = @RequestBody(
                    description = "Health staff to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "HealthStaff example",
                                    summary = "Full health staff user details",
                                    value = """
                                            {
                                              "firstName": "Esteban",
                                              "lastName": "Gómez",
                                              "birthDate": "1988-04-10",
                                              "email": "esteban.gomez@csu.org",
                                              "password": "medicoSeguro321",
                                              "registrationDate": "2025-06-10",
                                              "enabled": true,
                                              "specialty": "PSYCHOLOGY",
                                              "license": "MED-82451"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Health staff user created successfully"),
                    @ApiResponse(responseCode = "409", description = "Health staff with same email already exists")
            }
    )
    public Response create(HealthStaffCreateDTO dto, @Context UriInfo uriInfo) {
        HealthStaffDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update a health staff user",
            description = "Updates an existing health staff user",
            parameters = @Parameter(name = "id", description = "ID of the user to update"),
            requestBody = @RequestBody(
                    description = "Updated health staff data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update health staff example",
                                    summary = "Modified specialty and status",
                                    value = """
                                            {
                                              "firstName": "Esteban",
                                              "lastName": "Gómez",
                                              "birthDate": "1988-04-10",
                                              "email": "esteban.gomez@csu.org",
                                              "password": "nuevoPassword2025",
                                              "registrationDate": "2025-06-10",
                                              "enabled": false,
                                              "specialty": "PSYCHOLOGY",
                                              "license": "MED-82451"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Health staff updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Health staff user not found"),
                    @ApiResponse(responseCode = "409", description = "Another health staff user with same email already exists")
            }
    )
    public Response update(@PathParam("id") Long id, HealthStaffDTO dto) {
        HealthStaffDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @AdminOnly
    @Path("/{id}")
    @Operation(
            summary = "Delete a health staff user",
            description = "Deletes a health staff user by ID",
            parameters = @Parameter(name = "id", description = "ID of the health staff user"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
