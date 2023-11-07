package at.uni.innsbruck.htibot.rest.generated.model;

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



@JsonTypeName("BaseErrorModel")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen")
public class BaseErrorModel   {
  private @Valid Integer resultCode;
  private @Valid String message;

  /**
   * HTTP Status Code
   **/
  public BaseErrorModel resultCode(Integer resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  
  @ApiModelProperty(example = "404", value = "HTTP Status Code")
  @JsonProperty("resultCode")
  public Integer getResultCode() {
    return resultCode;
  }

  @JsonProperty("resultCode")
  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  /**
   * Error Message
   **/
  public BaseErrorModel message(String message) {
    this.message = message;
    return this;
  }

  
  @ApiModelProperty(example = "PersistenceException", value = "Error Message")
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseErrorModel baseErrorModel = (BaseErrorModel) o;
    return Objects.equals(this.resultCode, baseErrorModel.resultCode) &&
        Objects.equals(this.message, baseErrorModel.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseErrorModel {\n");
    
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

