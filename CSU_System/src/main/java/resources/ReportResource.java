package resources;

import dtos.ReportDTO;
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
import models.Report;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import services.ReportService;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Path("/reports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Report Resource")
public class ReportResource {

    @Inject
    private ReportService service;

    @GET
    @Operation(
            summary = "List all reports",
            responses = { @ApiResponse(responseCode = "200", description = "List retrieved") }
    )
    public Response getAll() {
        List<ReportDTO> list = service.getAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Find report by ID",
            parameters = @Parameter(name = "id", description = "Report ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report found"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        ReportDTO dto = service.getById(id);
        return Response.ok(dto).build();
    }

    @POST
    @Operation(
            summary = "Create a report (metadata only; file is attached separately)",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Create report",
                                    value = """
                                    {
                                      "name": "Weekly Vaccination Summary",
                                      "description": "Auto-captured dashboard summary",
                                      "authorId": 5,
                                      "visibleToAllHealthStaff": true,
                                      "sharedWithIds": [7, 11]
                                    }
                                    """
                            )
                    )
            ),
            responses = { @ApiResponse(responseCode = "201", description = "Created") }
    )
    public Response create(ReportDTO dto, @Context UriInfo uriInfo) {
        ReportDTO created = service.create(dto);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId().toString()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update report metadata",
            parameters = @Parameter(name = "id", description = "Report ID", required = true),
            requestBody = @RequestBody(required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public Response update(@PathParam("id") Long id, ReportDTO dto) {
        ReportDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete report",
            parameters = @Parameter(name = "id", description = "Report ID"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(
            summary = "Attach file to report",
            responses = {
                    @ApiResponse(responseCode = "200", description = "File attached"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public Response attachFile(
            @PathParam("id") Long id,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @Context HttpHeaders headers
    ) throws Exception {
        if (fileStream == null) throw new BadRequestException("Missing file");
        byte[] bytes = fileStream.readAllBytes();
        String fileName = fileDetail != null ? fileDetail.getFileName() : "report.pdf";
        String mime = headers != null ? headers.getHeaderString("Content-Type") : null;
        ReportDTO dto = service.attachFile(id, bytes, fileName, mime != null ? mime : "application/pdf");
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{id}/file")
    @Produces("application/pdf")
    @Operation(
            summary = "Download report file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "File stream"),
                    @ApiResponse(responseCode = "404", description = "Report or file not found")
            }
    )
    public Response downloadFile(@PathParam("id") Long id) {
        Report r = service.validateExistsForDownload(id);
        if (r.getFileContent() == null) throw new NotFoundException();
        String fname = r.getFileName() != null ? r.getFileName() : ("report-" + id + ".pdf");
        String mime = r.getFileMime() != null ? r.getFileMime() : "application/pdf";
        return Response.ok(r.getFileContent(), mime)
                .header("Content-Disposition", "attachment; filename=\"" + fname + "\"")
                .build();
    }

    @GET
    @Path("/public")
    @Operation(
            summary = "List public reports (visible to everyone)",
            responses = { @ApiResponse(responseCode = "200", description = "List retrieved") }
    )
    public Response getPublicReports() {
        List<ReportDTO> list = service.getPublicReports();
        return Response.ok(list).build();
    }

    @POST
    @Path("/{id}/share/{representativeId}")
    @Operation(
            summary = "Share report with a SocialOrgRepresentative",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Shared"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public Response share(@PathParam("id") Long id, @PathParam("representativeId") Long repId) {
        ReportDTO dto = service.shareWithRepresentative(id, repId);
        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}/share/{representativeId}")
    @Operation(
            summary = "Revoke report share with a SocialOrgRepresentative",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Revoked"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public Response revokeShare(@PathParam("id") Long id, @PathParam("representativeId") Long repId) {
        ReportDTO dto = service.revokeShareWithRepresentative(id, repId);
        return Response.ok(dto).build();
    }
}
