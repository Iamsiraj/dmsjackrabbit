package com.dms.jr.service;

import com.dms.jr.dto.MigrationUploadRequestDto;
import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.model.DocumentInfo;

import java.util.Optional;

public interface DocumentInfoService {

  DocumentInfo saveDocumentInfo(UploadRequestDto uploadRequestDto, Long lastestVersion);

  void deleteById(Integer id);

  DocumentInfo deleteByJcrId(String id);

  Optional<DocumentInfo> findByJcrId(String jcrId, boolean isDeleted);

  DocumentInfo saveDocumentInfo(MigrationUploadRequestDto migrationUploadRequestDto, Long latestVersion);

  Optional<DocumentInfo> findLatestDocByBasePathAndFileName(String basePath, String fileName);

}
