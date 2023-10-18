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

@RestController
@Slf4j
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileHandlerController {

    private final FileHandlerService fileHandlerService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("basePath") String basePath, @RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile file) {

        fileHandlerService.uploadFile(basePath, fileName, file);
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