package resources;

import dtos.CampaignDTO;
import interceptors.AdminOnly;
import interceptors.AllowedRoles;
import services.CampaignService;

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

import java.net.URI;
import java.util.List;

@Path("/campaigns")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Campaign Resource")
public class CampaignResource {

    @Inject
    private CampaignService service;

    @AllowedRoles({"Admin","HealthStaff"})
    @GET
    @Operation(
            summary = "List all campaigns",
            description = "Returns a list of all campaigns",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of campaigns retrieved successfully")
            }
    )
    public Response getAll() {
        List<CampaignDTO> campaigns = service.getAll();
        return Response.ok(campaigns).build();
    }

    @AllowedRoles({"Admin","HealthStaff"})
    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find campaign by ID",
            description = "Returns a campaign matching the given ID",
            parameters = @Parameter(name = "id", description = "ID of the campaign"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Campaign found"),
                    @ApiResponse(responseCode = "404", description = "Campaign not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        CampaignDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @AdminOnly
    @POST
    @Operation(
            summary = "Create a campaign",
            description = "Creates a new campaign and assigns a neighborhood",
            requestBody = @RequestBody(
                    description = "Campaign to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Complete campaign example",
                                    summary = "Includes name, date range, and neighborhood ID",
                                    value = """
                            {
                              "name": "Health Awareness 2025",
                              "startDate": "2025-07-01",
                              "endDate": "2025-07-15",
                              "neighborhoodId": 1
                            }
            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Campaign created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or malformed data"),
                    @ApiResponse(responseCode = "404", description = "Associated neighborhood not found"),
                    @ApiResponse(responseCode = "409", description = "Campaign with same name already exists")
            }
    )
    public Response create(CampaignDTO dto, @Context UriInfo uriInfo) {
        CampaignDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @AdminOnly
    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update a campaign",
            description = "Updates an existing campaign by its ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID of the campaign to update",
                    required = true
            ),
            requestBody = @RequestBody(
                    description = "Updated campaign data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update campaign example",
                                    summary = "Modifies date range and neighborhood",
                                    value = """
                                            {
                                              "name": "Health Awareness 2025",
                                              "startDate": "2025-07-10",
                                              "endDate": "2025-07-20",
                                              "neighborhoodId": 1
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Campaign updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or malformed data"),
                    @ApiResponse(responseCode = "404", description = "Campaign not found"),
                    @ApiResponse(responseCode = "409", description = "Another campaign with same name exists")
            }
    )
    public Response update(@PathParam("id") Long id, CampaignDTO dto) {
        CampaignDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @AdminOnly
    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete a campaign",
            description = "Deletes a campaign by its ID",
            parameters = @Parameter(name = "id", description = "ID of the campaign to delete"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Campaign deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Campaign not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
