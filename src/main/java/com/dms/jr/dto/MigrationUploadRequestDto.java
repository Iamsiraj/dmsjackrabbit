package com.dms.jr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MigrationUploadRequestDto {

  private String customerId;

  private String fileName;

  private String basePath;

  private String documentType;

  private String documentId;

  private String status;

  private String jcrId;
  private String revId;

  private String revName;
}
