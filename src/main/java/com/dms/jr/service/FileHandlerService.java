package com.dms.jr.service;

import com.dms.jr.dto.MigrationUploadRequestDto;
import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.dto.UploadResponseDto;
import com.dms.jr.model.DocumentInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileHandlerService {
  UploadResponseDto uploadFile(UploadRequestDto uploadRequestDto, MultipartFile file);

  void deleteFile(String basePath, String fileName);

  Resource downloadFile(String basePath, String fileName);

  void deleteFileByJcrId(String id);

  byte[] downloadFileByJcrId(String id);

  UploadResponseDto uploadFile(MigrationUploadRequestDto migrationUploadRequestDto, MultipartFile file);

}
