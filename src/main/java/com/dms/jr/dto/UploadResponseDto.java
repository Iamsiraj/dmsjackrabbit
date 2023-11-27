package com.dms.jr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UploadResponseDto {


    private String jcrId = "{dmsUuid}" + UUID.randomUUID();
    private String revId = "{dmsRev}" + UUID.randomUUID();

    private String fileName;

}
