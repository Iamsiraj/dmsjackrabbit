package com.dms.jr.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.jcr.RepositoryException;

public interface FileHandlerService {
    void uploadFile(String basePath, String fileName, MultipartFile file) throws RepositoryException;

    Resource downloadFile(String basePath, String fileName) throws RepositoryException;

}
