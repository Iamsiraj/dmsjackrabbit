package com.dms.jr.controller;

import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.exceptions.ServiceException;
import com.dms.jr.service.FileHandlerService;
import com.dms.jr.utils.ErrorCode;
import com.dms.jr.utils.ErrorMessages;
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
    public ResponseEntity<String> uploadFile(@RequestBody UploadRequestDto uploadRequestDto, @RequestParam("file") MultipartFile file) {

        if (StringUtils.isBlank(uploadRequestDto.getFileName())) {
            throw new ServiceException(ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "FILE NAME"));
        }

        if (StringUtils.isBlank(uploadRequestDto.getBasePath())) {
            throw new ServiceException(ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "BASE PATH"));
        }

        if (StringUtils.isBlank(uploadRequestDto.getCustomerId())) {
            throw new ServiceException(ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "CUSTOMER ID"));
        }

        if (StringUtils.isBlank(uploadRequestDto.getDocumentType())) {
            throw new ServiceException(ErrorCode.FIELD_REQUIRED, String.format(ErrorMessages.FIELD_REQUIRED, "DOCUMENT TYPE"));
        }

        if (file == null || file.isEmpty()) {
            throw new ServiceException(ErrorCode.FILE_REQUIRED, ErrorMessages.FILE_REQUIRED);
        }

        fileHandlerService.uploadFile(uploadRequestDto, file);
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName) {

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
    public ResponseEntity<?> deleteFile(@RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName) {

        fileHandlerService.deleteFile(basePath, fileName);
        return ResponseEntity.ok("Ok");
    }
}