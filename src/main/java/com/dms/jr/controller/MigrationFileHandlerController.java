package com.dms.jr.controller;

import com.dms.jr.dto.*;
import com.dms.jr.exceptions.ServiceException;
import com.dms.jr.service.FileHandlerService;
import com.dms.jr.utils.ErrorCode;
import com.dms.jr.utils.ErrorMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/migration-file")
@RequiredArgsConstructor
public class MigrationFileHandlerController {

  private final FileHandlerService fileHandlerService;

  @PostMapping("/upload")
  public ResponseEntity<UploadResponseDto> uploadFile(
      @RequestPart("dto") String dto, @RequestPart("file") MultipartFile file) {

    MigrationUploadRequestDto migrationUploadRequestDto =
        convertJsonStringToMigraionUploadRequestDto(dto);

    validateUploadRequestDto(migrationUploadRequestDto);

    validateMutlipartFile(file);

    UploadResponseDto uploadResponseDto =
        fileHandlerService.uploadFile(migrationUploadRequestDto, file);
    return ResponseEntity.ok(uploadResponseDto);
  }

  private MigrationUploadRequestDto convertJsonStringToMigraionUploadRequestDto(String dto) {
    MigrationUploadRequestDto migrationUploadRequestDto;
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      migrationUploadRequestDto = objectMapper.readValue(dto, MigrationUploadRequestDto.class);
    } catch (JsonProcessingException e) {
      log.error("FileHandlerController:: Error occurred while parsing JSON : {}", e.getMessage());
      throw new ServiceException(ErrorCode.JSON_PARSE_ERROR, ErrorMessages.JSON_PARSE_ERROR);
    }
    return migrationUploadRequestDto;
  }

  private void validateMutlipartFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.info(ErrorMessages.FIELD_REQUIRED);
      throw new ServiceException(ErrorCode.FILE_REQUIRED, ErrorMessages.FILE_REQUIRED);
    }
  }

  private void validateUploadRequestDto(MigrationUploadRequestDto dto) {
    if (StringUtils.isBlank(dto.getJcrId())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "JCR ID"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "JCR ID"));
    }
    if (StringUtils.isBlank(dto.getRevId())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "REV ID"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "REV ID"));
    }
    if (StringUtils.isBlank(dto.getRevName())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "REV NAME"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "REV NAME"));
    }
    if (StringUtils.isBlank(dto.getFileName())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "FILE NAME"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "FILE NAME"));
    }

    if (StringUtils.isBlank(dto.getBasePath())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "BASE PATH"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "BASE PATH"));
    }

    if (StringUtils.isBlank(dto.getCustomerId())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "CUSTOMER ID"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "CUSTOMER ID"));
    }

    if (StringUtils.isBlank(dto.getDocumentType())) {
      log.info(String.format(ErrorMessages.FIELD_REQUIRED, "DOCUMENT TYPE"));
      throw new ServiceException(
          ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "DOCUMENT TYPE"));
    }
  }
}
