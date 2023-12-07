package at.uni.innsbruck.htibot.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("rateConversation_200_response")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen")
public class RateConversation200Response   {
  private @Valid Integer resultCode;
  private @Valid String incidentReport;

  /**
   * The result code
   **/
  public RateConversation200Response resultCode(Integer resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  
  @ApiModelProperty(example = "200", value = "The result code")
  @JsonProperty("resultCode")
  public Integer getResultCode() {
    return resultCode;
  }

  @JsonProperty("resultCode")
  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  /**
   * If the user has closed the conversation, an incident report on the conversation is generated.
   **/
  public RateConversation200Response incidentReport(String incidentReport) {
    this.incidentReport = incidentReport;
    return this;
  }

  
  @ApiModelProperty(value = "If the user has closed the conversation, an incident report on the conversation is generated.")
  @JsonProperty("incidentReport")
  public String getIncidentReport() {
    return incidentReport;
  }

  @JsonProperty("incidentReport")
  public void setIncidentReport(String incidentReport) {
    this.incidentReport = incidentReport;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateConversation200Response rateConversation200Response = (RateConversation200Response) o;
    return Objects.equals(this.resultCode, rateConversation200Response.resultCode) &&
        Objects.equals(this.incidentReport, rateConversation200Response.incidentReport);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode, incidentReport);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateConversation200Response {\n");
    
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    incidentReport: ").append(toIndentedString(incidentReport)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}

