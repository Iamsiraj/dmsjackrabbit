package com.dms.jr.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileDownloadResponseDto {

  byte[] byteArray;
}
