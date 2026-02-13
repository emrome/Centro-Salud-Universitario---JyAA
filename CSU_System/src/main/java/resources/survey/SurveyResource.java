package resources.survey;

import interceptors.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataParam;
import services.survey.SurveyService;

@AdminOnly
@Path("/campaigns/{campaignId}/survey")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Tag(name = "Survey Import Resource")
public class SurveyResource {

    @Inject
    SurveyService surveyService;

    @POST
    @Operation(
            summary = "Import CSV files for surveys",
            description = "Uploads two CSV files: one for form data and another for branch data.",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA,
                            examples = @ExampleObject(name = "CSV Files", value = "formCsv, branchCsv")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "CSV files uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public Response upload(
            @FormDataParam("formCsv") InputStream formCsv,
            @FormDataParam("branchCsv") InputStream branchCsv,
            @FormDataParam("campaignId") Long campaignId,
            @FormDataParam("campaignCode") String campaignCode,
            @FormDataParam("neighborhoodName") String neighborhoodName
    ) {
        surveyService.create(formCsv, branchCsv, campaignId, campaignCode, neighborhoodName);
        return Response.ok().build();
    }

    @DELETE
    @Operation(
            summary = "Delete the survey associated with a campaign",
            description = "Removes the survey and all related question-answers and answers for a given campaign.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Survey deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Campaign not found")
            }
    )
    public Response delete(@PathParam("campaignId") Long campaignId) {
        surveyService.delete(campaignId);
        return Response.noContent().build();
    }
}