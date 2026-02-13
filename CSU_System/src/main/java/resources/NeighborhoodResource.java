package resources;

import dtos.NeighborhoodDTO;
import interceptors.AdminOnly;
import interceptors.AllowedRoles;
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
import services.NeighborhoodService;

import java.net.URI;
import java.util.List;

@Path("/neighborhoods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Neighborhood Resource")
public class NeighborhoodResource {

    @Inject
    private NeighborhoodService service;

    @AllowedRoles({"Admin","HealthStaff"})
    @GET
    @Operation(
            summary = "List all neighborhoods",
            description = "Returns a list of all not deleted neighborhoods",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of neighborhoods retrieved successfully")
            }
    )
    public Response getAll() {
        List<NeighborhoodDTO> neighborhoods = service.getAll();
        return Response.ok(neighborhoods).build();
    }

    @AllowedRoles({"Admin","HealthStaff"})
    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find neighborhood by ID",
            description = "Returns a neighborhood matching the given ID if it's not deleted",
            parameters = @Parameter(name = "id", description = "ID of the neighborhood"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Neighborhood found"),
                    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        NeighborhoodDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @AdminOnly
    @POST
    @Operation(
            summary = "Create a neighborhood",
            description = "Creates a new neighborhood with its geolocation polygon",
            requestBody = @RequestBody(
                    description = "Neighborhood to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Complete neighborhood example",
                                    summary = "Includes name, description and full list of coordinates",
                                    value = """
                                            {
                                              "name": "La Plata Centro",
                                              "description": "Historic downtown area with government buildings, shops, and restaurants",
                                              "geolocation": [
                                                {
                                                  "lat": -34.9205,
                                                  "lng": -57.9536
                                                },
                                                {
                                                  "lat": -34.9195,
                                                  "lng": -57.9520
                                                },
                                                {
                                                  "lat": -34.9210,
                                                  "lng": -57.9510
                                                },
                                                {
                                                  "lat": -34.9220,
                                                  "lng": -57.9525
                                                },
                                                {
                                                  "lat": -34.9205,
                                                  "lng": -57.9536
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Neighborhood created successfully"),
                    @ApiResponse(responseCode = "409", description = "Neighborhood with same name already exists"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or malformed data")
            }
    )
    public Response create(NeighborhoodDTO dto, @Context UriInfo uriInfo) {
        NeighborhoodDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @AdminOnly
    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update a neighborhood",
            description = "Updates an existing neighborhood by its ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID of the neighborhood to update",
                    required = true
            ),
            requestBody = @RequestBody(
                    description = "Updated neighborhood data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update neighborhood example",
                                    summary = "Full neighborhood update with new name and coordinates",
                                    value = """
                                            {
                                              "name": "La Plata Este",
                                              "description": "Updated name and boundaries for the eastern section",
                                              "geolocation": [
                                                { "lat": -34.9215, "lng": -57.9531 },
                                                { "lat": -34.9210, "lng": -57.9510 },
                                                { "lat": -34.9220, "lng": -57.9505 },
                                                { "lat": -34.9225, "lng": -57.9520 },
                                                { "lat": -34.9215, "lng": -57.9531 }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Neighborhood updated successfully"),
                    @ApiResponse(responseCode = "409", description = "Neighborhood name already exists"),
                    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
            }
    )
    public Response update(@PathParam("id") Long id, NeighborhoodDTO dto) {
        NeighborhoodDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @AdminOnly
    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete a neighborhood",
            description = "Deletes a neighborhood by its ID",
            parameters = @Parameter(name = "id", description = "ID of the neighborhood to delete"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Neighborhood deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
