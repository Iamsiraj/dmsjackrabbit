package com.dms.jr.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileHandlerService {
    void uploadFile(String basePath, String fileName, MultipartFile file);

    void deleteFile(String basePath,String fileName);

    Resource downloadFile(String basePath, String fileName);

}
