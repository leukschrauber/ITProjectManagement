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



@JsonTypeName("BaseSuccessModel")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen")
public class BaseSuccessModel   {
  private @Valid Integer resultCode;

  /**
   * HTTP Status Code
   **/
  public BaseSuccessModel resultCode(Integer resultCode) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseSuccessModel baseSuccessModel = (BaseSuccessModel) o;
    return Objects.equals(this.resultCode, baseSuccessModel.resultCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseSuccessModel {\n");
    
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

