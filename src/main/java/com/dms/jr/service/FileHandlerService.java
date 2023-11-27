package com.dms.jr.service;

import com.dms.jr.dto.UploadRequestDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileHandlerService {
    void uploadFile(UploadRequestDto uploadRequestDto ,MultipartFile file);

    void deleteFile(String basePath,String fileName);

    Resource downloadFile(String basePath, String fileName);

}
