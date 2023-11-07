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



@JsonTypeName("hasOpenConversation_200_response")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen")
public class HasOpenConversation200Response   {
  private @Valid Integer resultCode;
  private @Valid Boolean hasOpenConversation;

  /**
   * The result code
   **/
  public HasOpenConversation200Response resultCode(Integer resultCode) {
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
   * Whether the user has an unclosed conversation and did not request further conversation.
   **/
  public HasOpenConversation200Response hasOpenConversation(Boolean hasOpenConversation) {
    this.hasOpenConversation = hasOpenConversation;
    return this;
  }

  
  @ApiModelProperty(value = "Whether the user has an unclosed conversation and did not request further conversation.")
  @JsonProperty("hasOpenConversation")
  public Boolean getHasOpenConversation() {
    return hasOpenConversation;
  }

  @JsonProperty("hasOpenConversation")
  public void setHasOpenConversation(Boolean hasOpenConversation) {
    this.hasOpenConversation = hasOpenConversation;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HasOpenConversation200Response hasOpenConversation200Response = (HasOpenConversation200Response) o;
    return Objects.equals(this.resultCode, hasOpenConversation200Response.resultCode) &&
        Objects.equals(this.hasOpenConversation, hasOpenConversation200Response.hasOpenConversation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode, hasOpenConversation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HasOpenConversation200Response {\n");
    
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    hasOpenConversation: ").append(toIndentedString(hasOpenConversation)).append("\n");
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

