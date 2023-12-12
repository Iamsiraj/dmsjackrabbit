package com.dms.jr.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadResponseDto {

  private String jcrId;
  private String revId;
  private String revision;
  private String fileName;
  private String size;

  @Override
  public String toString() {
    return (jcrId != null ? jcrId : "null")
        + "###~###"
        + (revId != null ? revId : "null")
        + "###~###"
        + (revision != null ? revision : "null")
        + "###~###"
        + (fileName != null ? fileName : "null")
        + "###~###"
        + (size != null ? size : "null");
  }
}
