package resources;

import dtos.OrganizationDTO;
import dtos.people.SocialOrgRepresentativeDTO;
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
import services.OrganizationService;
import services.people.SocialOrgRepresentativeService;

import java.net.URI;
import java.util.List;

@Path("/organizations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Organization Resource")
public class OrganizationResource {

    @Inject
    private OrganizationService service;

    @Inject
    private SocialOrgRepresentativeService representativeService;

    @GET
    @Operation(
            summary = "List all organizations",
            description = "Returns a list of all organizations",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of organizations retrieved successfully")
            }
    )
    public Response getAll() {
        List<OrganizationDTO> organizations = service.getAll();
        return Response.ok(organizations).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find organization by ID",
            description = "Returns an organization matching the given ID",
            parameters = @Parameter(name = "id", description = "ID of the organization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Organization found"),
                    @ApiResponse(responseCode = "404", description = "Organization not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        OrganizationDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{id}/representatives")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRepresentativesByOrganization(@PathParam("id") Long orgId) {
        List<SocialOrgRepresentativeDTO> reps = representativeService.getByOrganization(orgId);
        return Response.ok(reps).build();
    }

    @POST
    @Operation(
            summary = "Create an organization",
            description = "Creates a new organization and optionally assigns a neighborhood",
            requestBody = @RequestBody(
                    description = "Organization to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Complete organization example",
                                    summary = "Includes name, address, main activity and neighborhood ID",
                                    value = """
                        {
                          "name": "La Huella Barrial",
                          "address": "Calle 10 N°123, Berisso",
                          "mainActivity": "HEALTHCARE",
                          "neighborhoodId": 1
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Organization created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or malformed data"),
                    @ApiResponse(responseCode = "404", description = "Associated neighborhood not found"),
                    @ApiResponse(responseCode = "409", description = "Organization with same name already exists")
            }
    )
    public Response create(OrganizationDTO dto, @Context UriInfo uriInfo) {
        OrganizationDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update an organization",
            description = "Updates an existing organization by its ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID of the organization to update",
                    required = true
            ),
            requestBody = @RequestBody(
                    description = "Updated organization data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update organization example",
                                    summary = "Modifies address, main activity and neighborhood",
                                    value = """
                        {
                          "name": "La Huella Barrial",
                          "address": "Calle 12 N°456, Berisso",
                          "mainActivity": "EDUCATION",
                          "neighborhoodId": 2
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Organization updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or malformed data"),
                    @ApiResponse(responseCode = "404", description = "Organization or neighborhood not found"),
                    @ApiResponse(responseCode = "409", description = "Another organization with same name exists")
            }
    )
    public Response update(@PathParam("id") Long id, OrganizationDTO dto) {
        OrganizationDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete an organization",
            description = "Deletes an organization by its ID (soft delete)",
            parameters = @Parameter(name = "id", description = "ID of the organization to delete"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Organization deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Organization not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
