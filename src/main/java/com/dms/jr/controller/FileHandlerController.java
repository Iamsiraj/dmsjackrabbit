package com.dms.jr.controller;

import com.dms.jr.service.FileHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jcr.RepositoryException;

@RestController
@Slf4j
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileHandlerController {

    private final FileHandlerService fileHandlerService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile file) {

        try {
            fileHandlerService.uploadFile(basePath, fileName, file);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName) {

        Resource resource = null;
        try {
            resource = fileHandlerService.downloadFile(basePath, fileName);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}