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

}
