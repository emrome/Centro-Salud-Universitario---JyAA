package resources.people;

import dtos.people.SocialOrgRepresentativeCreateDTO;
import dtos.people.SocialOrgRepresentativeDTO;
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
import services.people.SocialOrgRepresentativeService;

import java.net.URI;
import java.util.List;

@Path("/representatives")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "SocialOrgRepresentative Resource")
public class SocialOrgRepresentativeResource {

    @Inject
    private SocialOrgRepresentativeService service;

    @GET
    @AdminOnly
    @Operation(
            summary = "List all social organization representatives",
            description = "Returns all registered social organization representatives",
            responses = @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    )
    public Response getAll() {
        List<SocialOrgRepresentativeDTO> list = service.getAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Get representative by ID",
            description = "Fetches a social org representative by their ID",
            parameters = @Parameter(name = "id", description = "ID of the representative"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Representative found"),
                    @ApiResponse(responseCode = "404", description = "Representative not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        SocialOrgRepresentativeDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @POST
    @Operation(
            summary = "Create a representative",
            description = "Creates a new social org representative",
            requestBody = @RequestBody(
                    description = "Representative to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Representative example",
                                    summary = "Example representative data",
                                    value = """
                                            {
                                              "firstName": "Jorge",
                                              "lastName": "Medina",
                                              "birthDate": "1992-04-11",
                                              "email": "jmedina@ong.org",
                                              "password": "hash1234",
                                              "registrationDate": "2025-06-10",
                                              "enabled": true,
                                              "organizationId": 3
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Representative created successfully"),
                    @ApiResponse(responseCode = "409", description = "Email already in use")
            }
    )
    public Response create(SocialOrgRepresentativeCreateDTO dto, @Context UriInfo uriInfo) {
        SocialOrgRepresentativeDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update a representative",
            description = "Updates the information of an existing social org representative",
            requestBody = @RequestBody(
                    description = "Updated representative data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update representative example",
                                    summary = "Changes email and org",
                                    value = """
                                            {
                                              "firstName": "Jorge",
                                              "lastName": "Medina",
                                              "birthDate": "1992-04-11",
                                              "email": "jorge.medina@updated.org",
                                              "password": "newHash5678",
                                              "registrationDate": "2025-06-10",
                                              "enabled": false,
                                              "organizationId": 5
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Representative updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Representative not found"),
                    @ApiResponse(responseCode = "409", description = "Email already in use by another representative")
            }
    )
    public Response update(@PathParam("id") Long id, SocialOrgRepresentativeDTO dto) {
        SocialOrgRepresentativeDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @AdminOnly
    @Path("/{id}")
    @Operation(
            summary = "Delete a representative",
            description = "Deletes a social org representative by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Representative not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
