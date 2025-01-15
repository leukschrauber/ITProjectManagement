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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateConversation200Response rateConversation200Response = (RateConversation200Response) o;
    return Objects.equals(this.resultCode, rateConversation200Response.resultCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateConversation200Response {\n");
    
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
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

