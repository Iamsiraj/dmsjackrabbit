package com.dms.jr.service.impl;

import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.model.DocumentInfo;
import com.dms.jr.repository.DocumentInfoRepository;
import com.dms.jr.service.DocumentInfoService;
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
  public DocumentInfo saveDocumentInfo(UploadRequestDto uploadRequestDto) {
    log.info(
        "DocumentInfoServiceImpl:: saveDocumentInfo Document Name :{}"
            + uploadRequestDto.getBasePath()
            + uploadRequestDto.getFileName());
    DocumentInfo documentInfo = mapUploadRequestDtoToDocumentInfo(uploadRequestDto);

    return save(documentInfo);
  }

  private DocumentInfo save(DocumentInfo documentInfo) {
    return documentInfoRepository.save(documentInfo);
  }

  private static DocumentInfo mapUploadRequestDtoToDocumentInfo(UploadRequestDto uploadRequestDto) {
    log.info("DocumentInfoServiceImpl:: mapUploadRequestDtoToDocumentInfo");
    if (uploadRequestDto == null) {
      return null;
    }
    DocumentInfo documentInfo = new DocumentInfo();
    documentInfo.setCustomerId(uploadRequestDto.getCustomerId());
    documentInfo.setBasePath(uploadRequestDto.getBasePath());
    documentInfo.setFileName(uploadRequestDto.getFileName());
    documentInfo.setDocumentType(uploadRequestDto.getDocumentType());
    documentInfo.setStatus(uploadRequestDto.getStatus());
    documentInfo.setDocumentId(uploadRequestDto.getDocumentId());
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

  private Optional<DocumentInfo> findByJcrId(String jcrId, boolean isDeleted) {
    log.info("DocumentInfoServiceImpl:: findByJcrId id:{} and isDeleted :{}", jcrId, isDeleted);
    return documentInfoRepository.findByJcrIdAndIsDeleted(jcrId, isDeleted);
  }
}
