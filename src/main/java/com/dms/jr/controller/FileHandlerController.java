package com.dms.jr.controller;

import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.dto.UploadResponseDto;
import com.dms.jr.exceptions.ServiceException;
import com.dms.jr.service.FileHandlerService;
import com.dms.jr.utils.ErrorCode;
import com.dms.jr.utils.ErrorMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileHandlerController {

  private final FileHandlerService fileHandlerService;

  @PostMapping("/upload")
  public ResponseEntity<UploadResponseDto> uploadFile(
      @RequestPart("dto") String dto, @RequestPart("file") MultipartFile file) {

    UploadRequestDto uploadRequestDto = convertJsonStringToUploadRequestDto(dto);

    validateUploadRequestDto(uploadRequestDto);

    validateMutlipartFile(file);

    UploadResponseDto uploadResponseDto = fileHandlerService.uploadFile(uploadRequestDto, file);
    return ResponseEntity.ok(uploadResponseDto);
  }

  @GetMapping("/download")
  public ResponseEntity<?> downloadFile(
      @RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName) {

    Resource resource = null;
    resource = fileHandlerService.downloadFile(basePath, fileName);

    String contentType = "application/octet-stream";
    String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .body(resource);
  }

  @DeleteMapping
  public ResponseEntity<?> deleteFile(
      @RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName) {

    fileHandlerService.deleteFile(basePath, fileName);
    return ResponseEntity.ok("Ok");
  }

  private UploadRequestDto convertJsonStringToUploadRequestDto(String dto) {
    UploadRequestDto uploadRequestDto;
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      uploadRequestDto = objectMapper.readValue(dto, UploadRequestDto.class);
    } catch (JsonProcessingException e) {
      log.error("FileHandlerController:: Error occurred while parsing JSON : {}", e.getMessage());
      throw new ServiceException(ErrorCode.JSON_PARSE_ERROR, ErrorMessages.JSON_PARSE_ERROR);
    }
    return uploadRequestDto;
  }

  private void validateMutlipartFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.info(ErrorMessages.FIELD_REQUIRED);
      throw new ServiceException(ErrorCode.FILE_REQUIRED, ErrorMessages.FILE_REQUIRED);
    }
  }

  private void validateUploadRequestDto(UploadRequestDto dto) {
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
