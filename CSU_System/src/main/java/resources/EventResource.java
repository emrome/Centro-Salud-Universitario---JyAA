package resources;

import dtos.EventDTO;
import dtos.people.SurveyorDTO;
import interceptors.AdminOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import services.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.net.URI;
import java.util.List;

@AdminOnly
@Path("/campaign/{campaignId}/events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Event Resource")
public class EventResource {

    @Inject
    private EventService service;

    @GET
    @Operation(
            summary = "List all events",
            description = "Returns a list of all scheduled events.",
            parameters = @Parameter(name = "campaignId", description = "ID of the campaign to which the events belong", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Events successfully retrieved")
            }
    )
    public Response getAll(@PathParam("campaignId") Long campaignId) {
        List<EventDTO> events = service.getAll(campaignId);
        return Response.ok(events).build();
    }

    @GET
    @Path("/{eventId}")
    @Operation(
            summary = "Get a specific event by ID",
            description = "Returns a single event by its ID.",
            parameters = {
                    @Parameter(name = "campaignId", description = "ID of the campaign to which the event belongs", required = true),
                    @Parameter(name = "eventId", description = "ID of the event", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event successfully retrieved"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    public Response getById(@PathParam("campaignId") Long campaignId, @PathParam("eventId") Long eventId) {
        EventDTO event = service.getById(campaignId, eventId);
        return Response.ok(event).build();
    }

    @POST
    @Operation(
            summary = "Create a new event",
            description = "Creates a new event with date, zone and assigned surveyors.",
            parameters = @Parameter(name = "campaignId", description = "ID of the campaign to which the event belongs", required = true),
            requestBody = @RequestBody(
                    required = true,
                    description = "JSON object representing the new event",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {@ExampleObject(
                                    name = "Sample event in campaign range",
                                    value = """
                                            {
                                              "date": "2025-07-10",
                                              "zoneId": 1,
                                              "surveyorIds": [1, 2]
                                            }
                                            """
                            ),
                                    @ExampleObject(
                                            name = "Sample event not in range",
                                            value = """
                                                    {
                                                      "date": "2025-08-01",
                                                      "zoneId": 1,
                                                      "surveyorIds": [1, 2]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Event successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid event data or date outside campaign range"),
                    @ApiResponse(responseCode = "404", description = "Zone not found")
            }
    )
    public Response create(@PathParam("campaignId") Long campaignId, EventDTO dto, @Context UriInfo uriInfo) {
        EventDTO created = service.create(campaignId, dto);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{eventId}")
    @Operation(
            summary = "Update an existing event",
            description = "Updates an existing event's data and returns the updated object.",
            parameters = {
                    @Parameter(name = "campaignId", description = "ID of the campaign to which the event belongs", required = true),
                    @Parameter(name = "eventId", description = "ID of the event to update", required = true),
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "JSON object with updated event data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Updated event",
                                    value = """
                                            {
                                              "date": "2025-08-10",
                                              "zoneId": 4,
                                              "surveyorIds": [7, 8]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",description = "Event successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid event data or date outside campaign range"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    public Response update(@PathParam("campaignId") Long campaignId, @PathParam("eventId") Long eventId, EventDTO dto) {
        EventDTO updated = service.update(campaignId, eventId, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{eventId}")
    @Operation(
            summary = "Delete an event",
            description = "Deletes an existing event by its ID.",
            parameters = {
                    @Parameter(name = "campaignId", description = "ID of the campaign", required = true),
                    @Parameter(name = "eventId", description = "ID of the event to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Event successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    public Response delete(@PathParam("campaignId") Long campaignId, @PathParam("eventId") Long eventId) {
        service.delete(campaignId, eventId);
        return Response.noContent().build();
    }


    @GET
    @Path("/{eventId}/surveyors")
    @Operation(
            summary = "Get surveyors for an event",
            description = "Returns a list of surveyors assigned to a specific event.",
            parameters = @Parameter(name = "eventId", description = "ID of the event", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Surveyors successfully retrieved"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    public Response getSurveyors(@PathParam("eventId") Long eventId) {
        List<SurveyorDTO> dtos = service.getSurveyorsForEvent(eventId);
        return Response.ok(dtos).build();
    }
}
