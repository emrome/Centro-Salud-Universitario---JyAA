package resources;

import interceptors.AdminOnly;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import services.ZoneService;
import dtos.ZoneDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.net.URI;
import java.util.List;

@AdminOnly
@Path("/neighborhoods/{neighborhoodId}/zones")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Zone Resource")
public class ZoneResource {

    @Inject
    private ZoneService service;

    @GET
    @Operation(
            summary = "List all zones of a neighborhood",
            description = "Returns a list of all not deleted zones that belong to the specified neighborhood.",
            parameters = {
                    @Parameter(name = "neighborhoodId", description = "ID of the neighborhood", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zones successfully retrieved"),
                    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
            }
    )
    public Response getAll(@PathParam("neighborhoodId") Long neighborhoodId) {
        List<ZoneDTO> zones = service.getZonesByNeighborhood(neighborhoodId);
        return Response.ok(zones).build();
    }

    @GET
    @Path("/{zoneId}")
    @Operation(
            summary = "Get a specific zone by ID",
            description = "Returns a zone by its ID within a specific neighborhood.",
            parameters = {
                    @Parameter(name = "neighborhoodId", description = "ID of the neighborhood", required = true),
                    @Parameter(name = "zoneId", description = "ID of the zone", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zone successfully retrieved"),
                    @ApiResponse(responseCode = "404", description = "Zone or neighborhood not found")
            }
    )
    public Response getById(@PathParam("neighborhoodId") Long neighborhoodId,
                            @PathParam("zoneId") Long zoneId) {
        ZoneDTO dto = service.getZoneInNeighborhood(neighborhoodId, zoneId);
        return Response.ok(dto).build();
    }

    @POST
    @Operation(
            summary = "Create a new zone in a neighborhood",
            description = "Creates a new zone associated with the specified neighborhood ID and its coordinates. Returns the created zone and a Location header.",
            parameters = {
                    @Parameter(name = "neighborhoodId", description = "ID of the neighborhood", required = true)
            },
            requestBody = @RequestBody(
                    description = "JSON object representing the new zone",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Sample zone",
                                    summary = "Zone creation example",
                                    value = """
                                            {
                                              "name": "East Zone",
                                              "coordinates": [
                                                { "lat": -34.9214, "lng": -57.9544 },
                                                { "lat": -34.9220, "lng": -57.9530 }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Zone successfully created"),
                    @ApiResponse(responseCode = "404", description = "Neighborhood not found"),
                    @ApiResponse(responseCode = "409", description = "Zone with the same name already exists")
            }
    )
    public Response create(@PathParam("neighborhoodId") Long neighborhoodId, ZoneDTO dto, @Context UriInfo uriInfo) {
        ZoneDTO created = service.createInNeighborhood(neighborhoodId, dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{zoneId}")
    @Operation(
            summary = "Update an existing zone in a neighborhood",
            description = "Updates the specified zone's data and returns the updated zone.",
            parameters = {
                    @Parameter(name = "neighborhoodId", description = "ID of the neighborhood", required = true),
                    @Parameter(name = "zoneId", description = "ID of the zone", required = true)
            },
            requestBody = @RequestBody(
                    description = "JSON object with updated zone data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Updated zone",
                                            value = """
                                                    {
                                                      "name": "Central Zone",
                                                      "coordinates": [
                                                        { "lat": -34.9215, "lng": -57.9540 },
                                                        { "lat": -34.9225, "lng": -57.9535 }
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Duplicate zone",
                                            summary = "Triggers 409 error if zone name already exists in neighborhood",
                                            value = """
                                                    {
                                                      "name": "East Zone",
                                                      "coordinates": [
                                                        { "lat": -34.9210, "lng": -57.9540 }
                                                      ]
                                                    }
                                            """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zone successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Zone or neighborhood not found")
            }
    )
    public Response update(@PathParam("neighborhoodId") Long neighborhoodId,
                           @PathParam("zoneId") Long zoneId, ZoneDTO dto) {
        ZoneDTO updated = service.updateInNeighborhood(neighborhoodId, zoneId, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{zoneId}")
    @Operation(
            summary = "Delete a zone from a neighborhood",
            description = "Deletes the specified zone from the given neighborhood. No content is returned.",
            parameters = {
                    @Parameter(name = "neighborhoodId", description = "ID of the neighborhood", required = true),
                    @Parameter(name = "zoneId", description = "ID of the zone", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zone successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Zone or neighborhood not found")
            }
    )
    public Response delete(@PathParam("neighborhoodId") Long neighborhoodId,
                           @PathParam("zoneId") Long zoneId) {
        service.deleteInNeighborhood(neighborhoodId, zoneId);
        return Response.noContent().build();
    }
}
