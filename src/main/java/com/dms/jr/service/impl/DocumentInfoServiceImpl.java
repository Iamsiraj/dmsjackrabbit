package com.dms.jr.service.impl;

import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.model.DocumentInfo;
import com.dms.jr.repository.DocumentInfoRepository;
import com.dms.jr.service.DocumentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentInfoServiceImpl implements DocumentInfoService {

  private final DocumentInfoRepository documentInfoRepository;

  @Override
  public DocumentInfo getById(Integer id) {
    return null;
  }

  @Override
  public List<DocumentInfo> getAll() {
    return null;
  }

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
}
