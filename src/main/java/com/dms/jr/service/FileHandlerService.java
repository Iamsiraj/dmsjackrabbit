package com.dms.jr.service;

import com.dms.jr.dto.FileDownloadResponseDto;
import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.dto.UploadResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileHandlerService {
  UploadResponseDto uploadFile(UploadRequestDto uploadRequestDto, MultipartFile file);

  void deleteFile(String basePath, String fileName);

  Resource downloadFile(String basePath, String fileName);

  void deleteFileByJcrId(String id);

  FileDownloadResponseDto downloadFileByJcrId(String id);
}
