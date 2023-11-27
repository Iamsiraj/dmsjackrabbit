package com.dms.jr.service;

import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.model.DocumentInfo;

import java.util.List;

public interface DocumentInfoService {

    DocumentInfo getById(Integer id);

    List<DocumentInfo> getAll();

    DocumentInfo saveDocumentInfo(UploadRequestDto uploadRequestDto);

    void deleteById(Integer id);
}
