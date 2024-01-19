package com.dms.jr.service.impl;

import com.dms.jr.dto.MigrationUploadRequestDto;
import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.model.DocumentInfo;
import com.dms.jr.repository.DocumentInfoRepository;
import com.dms.jr.service.DocumentInfoService;
import com.dms.jr.util.JCRUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentInfoServiceImpl implements DocumentInfoService {

  private final DocumentInfoRepository documentInfoRepository;

  @Override
  public DocumentInfo saveDocumentInfo(UploadRequestDto uploadRequestDto, Long latestVersion) {
    log.info(
        "DocumentInfoServiceImpl:: saveDocumentInfo Document Name :{}"
            + uploadRequestDto.getBasePath()
            + uploadRequestDto.getFileName());
    DocumentInfo documentInfo = mapUploadRequestDtoToDocumentInfo(uploadRequestDto, latestVersion);

    return save(documentInfo);
  }

  @Override
  public DocumentInfo saveDocumentInfo(MigrationUploadRequestDto migrationUploadRequestDto, Long latestVersion) {
    log.info(
        "DocumentInfoServiceImpl:: saveDocumentInfo Document Name :{}"
            + migrationUploadRequestDto.getBasePath()
            + migrationUploadRequestDto.getFileName());
    DocumentInfo documentInfo = mapUploadRequestDtoToDocumentInfo(migrationUploadRequestDto, latestVersion);

    return save(documentInfo);
  }

  @Override
  public Optional<DocumentInfo> findLatestDocByBasePathAndFileName(String basePath, String fileName) {
    return documentInfoRepository.findFirstByBasePathAndFileNameOrderByVersionDesc(basePath, fileName);
  }

  private DocumentInfo mapUploadRequestDtoToDocumentInfo(
      MigrationUploadRequestDto migrationUploadRequestDto, Long latestVersion) {
    log.info("DocumentInfoServiceImpl:: mapUploadRequestDtoToDocumentInfo");
    if (migrationUploadRequestDto == null) {
      return null;
    }
    DocumentInfo documentInfo = new DocumentInfo();
    documentInfo.setCustomerId(migrationUploadRequestDto.getCustomerId());
    documentInfo.setBasePath(migrationUploadRequestDto.getBasePath());
    documentInfo.setFileName(migrationUploadRequestDto.getFileName());
    documentInfo.setDocumentType(migrationUploadRequestDto.getDocumentType());
//    documentInfo.setStatus(migrationUploadRequestDto.getStatus());
//    documentInfo.setDocumentId(migrationUploadRequestDto.getDocumentId());
    documentInfo.setJcrId(migrationUploadRequestDto.getJcrId());
    documentInfo.setRevisionId(migrationUploadRequestDto.getRevId());
    documentInfo.setRevisionName(migrationUploadRequestDto.getRevName());
    documentInfo.setVersion(latestVersion);
    return documentInfo;
  }

  private DocumentInfo save(DocumentInfo documentInfo) {
    return documentInfoRepository.save(documentInfo);
  }

  private static DocumentInfo mapUploadRequestDtoToDocumentInfo(UploadRequestDto uploadRequestDto, Long version) {
    log.info("DocumentInfoServiceImpl:: mapUploadRequestDtoToDocumentInfo");
    if (uploadRequestDto == null) {
      return null;
    }
    DocumentInfo documentInfo = new DocumentInfo();
    documentInfo.setCustomerId(uploadRequestDto.getCustomerId());
    documentInfo.setBasePath(uploadRequestDto.getBasePath());
    documentInfo.setFileName(uploadRequestDto.getFileName());
    documentInfo.setDocumentType(uploadRequestDto.getDocumentType());
//    documentInfo.setStatus(uploadRequestDto.getStatus());
//    documentInfo.setDocumentId(uploadRequestDto.getDocumentId());
    documentInfo.setJcrId(JCRUtil.generateJCRId());
    documentInfo.setVersion(version);
    documentInfo.setRevisionId(JCRUtil.generateJCRRevId());
    return documentInfo;
  }

  @Override
  public void deleteById(Integer id) {
    log.info("DocumentInfoServiceImpl:: deleteById id:{}", id);
    documentInfoRepository.deleteById(id);
  }

  @Override
  @Transactional
  public DocumentInfo deleteByJcrId(String id) {
    log.info("DocumentInfoServiceImpl:: deleteByJcrId id:{}", id);
    Optional<DocumentInfo> optionalDocumentInfo = findByJcrId(id, false);

    if (optionalDocumentInfo.isEmpty()) {
      return null;
    }

    log.info("DocumentInfoServiceImpl:: optionalDocumentInfo.isPresent() id:{}", id);
    DocumentInfo documentInfo = optionalDocumentInfo.get();
    documentInfo.setIsDeleted(true);
    return documentInfo;
  }

  public Optional<DocumentInfo> findByJcrId(String jcrId, boolean isDeleted) {
    log.info("DocumentInfoServiceImpl:: findByJcrId id:{} and isDeleted :{}", jcrId, isDeleted);
    return documentInfoRepository.findByJcrIdAndIsDeleted(jcrId, isDeleted);
  }
}
