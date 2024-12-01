package at.uni.innsbruck.htibot.rest.generated.api;

import at.uni.innsbruck.htibot.rest.generated.model.BaseErrorModel;
import at.uni.innsbruck.htibot.rest.generated.model.BaseSuccessModel;
import at.uni.innsbruck.htibot.rest.generated.model.GetAnswer200Response;
import at.uni.innsbruck.htibot.rest.generated.model.HasOpenConversation200Response;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import at.uni.innsbruck.htibot.rest.generated.model.RateConversation200Response;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import io.swagger.annotations.*;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

@Path("/htibot")
@Api(description = "the htibot API")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen")
public interface HtibotApi {

    @PUT
    @Path("/continueConversation")
    @Produces({ "application/json" })
    @ApiOperation(value = "Requests further conversation in the ongoing conversation", notes = "Requests further conversation in the ongoing conversation enabling to follow-up on messages.", authorizations = {
        
        @Authorization(value = "apiKeyAuth")
         }, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = BaseSuccessModel.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 404, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 400, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 500, message = "Error.", response = BaseErrorModel.class) })
    Response continueConversation(@QueryParam("userId") @NotNull  @ApiParam("The user id as determined by the caller")  String userId);

    @GET
    @Path("/getAnswer")
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves an answer to a user prompt.", notes = "Retrieves relevant internal documentation and generates an answer using a Large Language Model.", authorizations = {
        
        @Authorization(value = "apiKeyAuth")
         }, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = GetAnswer200Response.class),
        @ApiResponse(code = 400, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 409, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 500, message = "Error.", response = BaseErrorModel.class) })
    Response getAnswer(@QueryParam("prompt") @NotNull  @ApiParam("The prompt of the user")  String prompt,@QueryParam("userId") @NotNull  @ApiParam("The user id as determined by the caller")  String userId,@QueryParam("language") @NotNull @DefaultValue("English")  @ApiParam("The language for the operation.")  LanguageEnum language);

    @GET
    @Path("/hasOpenConversation")
    @Produces({ "application/json" })
    @ApiOperation(value = "Checks whether user currently has any open conversation with the Bot Backend.", notes = "A user that has not closed his conversation yet and that has not requested to continue the current conversation has an open conversation.", authorizations = {
        
        @Authorization(value = "apiKeyAuth")
         }, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = HasOpenConversation200Response.class),
        @ApiResponse(code = 400, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 500, message = "Error.", response = BaseErrorModel.class) })
    Response hasOpenConversation(@QueryParam("userId") @NotNull  @ApiParam("The user id as determined by the caller")  String userId);

    @OPTIONS
    @Path("/continueConversation")
    @ApiOperation(value = "CORS support", notes = "Enable CORS by returning correct headers", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A successful response to the preflight request including CORS headers", response = Void.class) })
    Response htibotContinueConversationOptions();

    @OPTIONS
    @Path("/getAnswer")
    @ApiOperation(value = "CORS support", notes = "Enable CORS by returning correct headers", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A successful response to the preflight request including CORS headers", response = Void.class) })
    Response htibotGetAnswerOptions();

    @OPTIONS
    @Path("/hasOpenConversation")
    @ApiOperation(value = "CORS support", notes = "Enable CORS by returning correct headers", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A successful response to the preflight request including CORS headers", response = Void.class) })
    Response htibotHasOpenConversationOptions();

    @OPTIONS
    @Path("/rateConversation")
    @ApiOperation(value = "CORS support", notes = "Enable CORS by returning correct headers", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A successful response to the preflight request including CORS headers", response = Void.class) })
    Response htibotRateConversationOptions();

    @PUT
    @Path("/rateConversation")
    @Produces({ "application/json" })
    @ApiOperation(value = "Rates the currently open conversation as positive or negative.", notes = "Rates the currently open conversation as positive or negative.", authorizations = {
        
        @Authorization(value = "apiKeyAuth")
         }, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = RateConversation200Response.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 404, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 409, message = "Error.", response = BaseErrorModel.class),
        @ApiResponse(code = 500, message = "Error.", response = BaseErrorModel.class) })
    Response rateConversation(@QueryParam("userId") @NotNull  @ApiParam("The user id as determined by the caller")  String userId,@QueryParam("rating") @NotNull  @ApiParam("The rating of the conversation")  Boolean rating);
}
