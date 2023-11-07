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



@JsonTypeName("getAnswer_200_response")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen")
public class GetAnswer200Response   {
  private @Valid Integer resultCode;
  private @Valid String answer;

  /**
   * The result code
   **/
  public GetAnswer200Response resultCode(Integer resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  
  @ApiModelProperty(example = "0", value = "The result code")
  @JsonProperty("resultCode")
  public Integer getResultCode() {
    return resultCode;
  }

  @JsonProperty("resultCode")
  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  /**
   * The answer to the users prompt.
   **/
  public GetAnswer200Response answer(String answer) {
    this.answer = answer;
    return this;
  }

  
  @ApiModelProperty(example = "Tomorrow it is gonna be 36 degrees and it gets even hotter.", value = "The answer to the users prompt.")
  @JsonProperty("answer")
  public String getAnswer() {
    return answer;
  }

  @JsonProperty("answer")
  public void setAnswer(String answer) {
    this.answer = answer;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetAnswer200Response getAnswer200Response = (GetAnswer200Response) o;
    return Objects.equals(this.resultCode, getAnswer200Response.resultCode) &&
        Objects.equals(this.answer, getAnswer200Response.answer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode, answer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetAnswer200Response {\n");
    
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    answer: ").append(toIndentedString(answer)).append("\n");
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

