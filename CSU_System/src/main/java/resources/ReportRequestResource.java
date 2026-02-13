package resources;

import dtos.ReportDTO;
import dtos.ReportRequestDTO;
import interceptors.HealthOnly;
import interceptors.RepresentativeOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import services.ReportRequestService;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/report-requests")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Report Request Resource")
public class ReportRequestResource {

    @Inject
    private ReportRequestService service;

    @Context
    private HttpServletRequest httpRequest;

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find report request by ID",
            parameters = @Parameter(name = "id", description = "Request ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request found"),
                    @ApiResponse(responseCode = "404", description = "Request not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        ReportRequestDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @RepresentativeOnly
    @GET
    @Path("/requester/me")
    @Operation(
            summary = "List requests for current SocialOrgRepresentative",
            responses = { @ApiResponse(responseCode = "200", description = "List retrieved") }
    )
    public Response getByRequesterMe() {
        String userIdStr = (String) httpRequest.getAttribute("userId");
        if (userIdStr == null) throw new ForbiddenException("No user in context");
        Long requesterId = Long.parseLong(userIdStr);
        List<ReportRequestDTO> list = service.getByRequester(requesterId);
        return Response.ok(list).build();
    }

    @HealthOnly
    @GET
    @Path("/pending")
    @Operation(
            summary = "List all pending requests for HealthStaff (shared pool)",
            responses = { @ApiResponse(responseCode = "200", description = "List retrieved") }
    )
    public Response getPendingForHealth() {
        List<ReportRequestDTO> list = service.getPendingForHealth();
        return Response.ok(list).build();
    }

    @RepresentativeOnly
    @POST
    @Operation(
            summary = "Create a report request",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Create request",
                                    value = """
                                    {
                                      "description": "Por favor, reporte mensual de casos de dengue"
                                    }
                                    """
                            )
                    )
            ),
            responses = { @ApiResponse(responseCode = "201", description = "Created") }
    )
    public Response create(ReportRequestDTO dto, @Context UriInfo uriInfo) {
        String userIdStr = (String) httpRequest.getAttribute("userId");
        if (userIdStr == null) throw new ForbiddenException("No user in context");
        Long currentUserId = Long.parseLong(userIdStr);

        ReportRequestDTO in = new ReportRequestDTO();
        in.setRequesterId(currentUserId);
        in.setDescription(dto != null ? dto.getDescription() : null);

        ReportRequestDTO created = service.create(in);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId().toString()).build();
        return Response.created(location).entity(created).build();
    }

    public static class RejectBody {
        public String reason;
    }

    @HealthOnly
    @POST
    @Path("/{id}/reject")
    @Operation(
            summary = "Reject a request",
            requestBody = @RequestBody(
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Reject request",
                                    value = """
                                    { "reason": "No hay datos suficientes para generar el reporte" }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rejected"),
                    @ApiResponse(responseCode = "404", description = "Request not found")
            }
    )
    public Response reject(@PathParam("id") Long id, RejectBody body) {
        String reason = body != null ? body.reason : null;
        String userIdStr = (String) httpRequest.getAttribute("userId");
        if (userIdStr == null) throw new ForbiddenException("No user in context");
        Long resolverId = Long.parseLong(userIdStr);
        ReportRequestDTO dto = service.reject(id, reason, resolverId);
        return Response.ok(dto).build();
    }

    @HealthOnly
    @POST
    @Path("/{id}/complete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response completeWithReport(
            @PathParam("id") Long id,
            @FormDataParam("name") String name,
            @FormDataParam("description") String description,
            @FormDataParam("visibleToAllHealthStaff") Boolean visibleToAllHealthStaff,
            @FormDataParam("isPublic") Boolean isPublic,
            @FormDataParam("sharedWithIds") String sharedWithIdsCsv,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
    ) throws Exception {
        if (fileStream == null) throw new BadRequestException("Missing file");
        String userIdStr = (String) httpRequest.getAttribute("userId");
        if (userIdStr == null) throw new ForbiddenException("No user in context");
        Long authorId = Long.parseLong(userIdStr);

        byte[] bytes = fileStream.readAllBytes();
        String fileName = fileDetail != null ? fileDetail.getFileName() : "reporte";
        String fileMime = guessMime(fileName);

        ReportDTO meta = new ReportDTO();
        meta.setName(name);
        meta.setDescription(description);
        meta.setVisibleToAllHealthStaff(visibleToAllHealthStaff == null ? true : visibleToAllHealthStaff);
        meta.setPublicVisible(Boolean.TRUE.equals(isPublic));
        meta.setAuthorId(authorId);

        ReportRequestDTO dto = service.completeWithReport(id, meta, bytes, fileName, fileMime, authorId);
        return Response.ok(dto).build();
    }

    private static String guessMime(String filename) {
        if (filename == null) return "application/octet-stream";
        String f = filename.toLowerCase();
        if (f.endsWith(".pdf")) return "application/pdf";
        if (f.endsWith(".png")) return "image/png";
        if (f.endsWith(".jpg") || f.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    @HealthOnly
    @GET
    @Path("/resolved")
    public Response getResolvedForHealth() {
        List<ReportRequestDTO> list = service.getResolvedForHealth();
        return Response.ok(list).build();
    }

    @HealthOnly
    @GET
    @Path("/rejected")
    public Response getRejectedForHealth() {
        List<ReportRequestDTO> list = service.getRejectedForHealth();
        return Response.ok(list).build();
    }
}
