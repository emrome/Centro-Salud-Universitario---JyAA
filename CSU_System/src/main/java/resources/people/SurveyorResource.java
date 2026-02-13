package resources.people;

import dtos.people.SurveyorDTO;
import interceptors.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
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
import services.people.SurveyorService;

import java.net.URI;
import java.util.List;

@AdminOnly
@Path("/surveyors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Surveyor Resource")
public class SurveyorResource {
    @Inject
    private SurveyorService service;

    @GET
    @Operation(
            summary = "List all surveyors",
            description = "Returns a list of all not deleted surveyors in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of surveyors retrieved successfully")
            }
    )
    public Response getAll() {
        List<SurveyorDTO> surveyors = service.getAll();
        return Response.ok(surveyors).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Get a surveyor by ID",
            description = "Returns the surveyor matching the provided ID if it's not deleted",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Surveyor found"),
                    @ApiResponse(responseCode = "404", description = "Surveyor not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        SurveyorDTO surveyor = service.getById(id);
        return Response.ok(surveyor).build();
    }

    @POST
    @Operation(
            summary = "Create a new surveyor",
            description = "Registers a new surveyor with personal and demographic data",
            requestBody = @RequestBody(
                    description = "Surveyor data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Complete surveyor",
                                            summary = "All fields filled",
                                            value = """
                                                    {
                                                      "firstName": "Juan Pedro",
                                                      "lastName": "Torres",
                                                      "dni": "34899123",
                                                      "birthDate": "1992-05-15",
                                                      "gender": "MAN_CIS",
                                                      "occupation": "VOLUNTEER"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Partial surveyor",
                                            summary = "Only required fields filled",
                                            value = """
                                                    {
                                                      "firstName": "Juampe",
                                                      "lastName": "Torres",
                                                      "dni": "12345678"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Surveyor created successfully")
            }
    )
    public Response create(SurveyorDTO dto, @Context UriInfo uriInfo) {
        SurveyorDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update a surveyor",
            description = "Updates the data of a surveyor by ID",
            requestBody = @RequestBody(
                    description = "Updated surveyor data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Update surveyor",
                                    summary = "Full surveyor update",
                                    value = """
                {
                  "firstName": "Juampe",
                  "lastName": "Torres",
                  "dni": "34899123",
                  "birthDate": "1992-05-15",
                  "gender": "MAN_CIS",
                  "occupation": "VOLUNTEER"
                }
                """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Surveyor updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Surveyor not found")
            }
    )
    public Response update(@PathParam("id") Long id, SurveyorDTO dto) {
        SurveyorDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete a surveyor",
            description = "Deletes a surveyor by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Surveyor deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Surveyor not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
