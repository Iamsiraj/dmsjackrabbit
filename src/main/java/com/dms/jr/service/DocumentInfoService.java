package com.dms.jr.service;

import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.model.DocumentInfo;

public interface DocumentInfoService {

  DocumentInfo saveDocumentInfo(UploadRequestDto uploadRequestDto);

  void deleteById(Integer id);

  DocumentInfo deleteByJcrId(String id);
}
