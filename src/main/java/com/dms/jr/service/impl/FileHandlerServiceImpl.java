package com.dms.jr.service.impl;

import com.dms.jr.bean.FileResponse;
import com.dms.jr.dto.MigrationUploadRequestDto;
import com.dms.jr.dto.UploadRequestDto;
import com.dms.jr.dto.UploadResponseDto;
import com.dms.jr.exceptions.ServiceException;
import com.dms.jr.helper.RepositoryHelper;
import com.dms.jr.model.DocumentInfo;
import com.dms.jr.repository.DocumentInfoRepository;
import com.dms.jr.service.DocumentInfoService;
import com.dms.jr.service.FileHandlerService;
import com.dms.jr.util.JCRUtil;
import com.dms.jr.utils.ErrorCode;
import com.dms.jr.utils.ErrorMessages;
import com.dms.jr.utils.JackrabbitConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileHandlerServiceImpl implements FileHandlerService {
  private final Repository repository;

  private final DocumentInfoService documentInfoService;


  @Override
  public UploadResponseDto uploadFile(UploadRequestDto uploadRequestDto, MultipartFile file) {
    Optional<DocumentInfo> optionalDoc = documentInfoService.findLatestDocByBasePathAndFileName(uploadRequestDto.getBasePath(), uploadRequestDto.getFileName());
    String basePath = JCRUtil.generateBasePath(uploadRequestDto.getBasePath());
    String fileName = uploadRequestDto.getFileName();
    Long version = JCRUtil.getVersion(optionalDoc);
    log.info("BasePath: {},File name : {}", basePath, fileName);
    File newFile = null;
    Long totalSpace;

    // Create a JCR session
    Session session = getSession(repository);
    DocumentInfo documentInfo = documentInfoService.saveDocumentInfo(uploadRequestDto, version);

    try {
      newFile = convertMultipartFileToFile(fileName, file.getBytes());
      totalSpace = newFile.getTotalSpace();
      RepositoryHelper.addFileNode(session, JCRUtil.generatePathWithVersion(basePath, version), newFile, JackrabbitConstants.USER);
    } catch (ServiceException e) {
      documentInfoService.deleteById(documentInfo.getId());
      throw new ServiceException(e.getCode(), e.getMessage());
    } catch (RepositoryException | IOException e) {
      documentInfoService.deleteById(documentInfo.getId());
      throw new ServiceException(
          ErrorCode.FILE_UPLOAD, ErrorMessages.FILE_UPLOAD + ": " + e.getMessage());
    } finally {
      newFile.delete();
      log.info("Local file deleted successfully.");
    }

    sessionSave(session);
    sessionLogout(session);

    return UploadResponseDto.builder()
        .jcrId(documentInfo.getJcrId())
        .revId(documentInfo.getRevisionId())
        .fileName(documentInfo.getFileName())
        .size(String.valueOf(totalSpace))
        .revision(documentInfo.getRevisionName())
        .build();
  }

  @Override
  public Resource downloadFile(String basePath, String fileName) {
    Session session = getSession(repository);

    FileResponse fileContents;
    try {
      fileContents = RepositoryHelper.getFileContents(session, basePath, fileName);
    } catch (ServiceException e) {
      throw new ServiceException(e.getCode(), e.getMessage());
    } catch (IOException | RepositoryException e) {
      throw new ServiceException(ErrorCode.FILE_DOWNLOAD, ErrorMessages.FILE_DOWNLOAD);
    }

    File newFile = convertMultipartFileToFile(fileName, fileContents.getBytes());


    Resource resource = new FileSystemResource(newFile);

    sessionSave(session);

    sessionLogout(session);

    return resource;
  }

  @Override
  public void deleteFileByJcrId(String id) {
    log.info("FileHandlerServiceImpl:: deleteFileByJcrId id:{}", id);
    DocumentInfo documentInfo = documentInfoService.deleteByJcrId(id);

    if (Objects.nonNull(documentInfo)) {
      log.info(
          "FileHandlerServiceImpl:: deleteFileByJcrId deleting document from Jackrabbit id:{}", id);
      deleteFile(JCRUtil.generatePathWithVersion(documentInfo.getBasePath(), documentInfo.getVersion()), documentInfo.getFileName());
    }
  }

  @Override
  public void deleteFile(String basePath, String fileName) {
    Session session = getSession(repository);

    try {
      RepositoryHelper.removeFileContents(session, JCRUtil.generateBasePath(basePath), fileName);
    } catch (ServiceException e) {
      throw new ServiceException(e.getCode(), e.getMessage());
    } catch (RepositoryException e) {
      throw new ServiceException(ErrorCode.FILE_DELETE_ERROR, ErrorMessages.FILE_DELETE_ERROR);
    }
    sessionSave(session);
    sessionLogout(session);
  }

  @Override
  public byte[] downloadFileByJcrId(String id) {
    log.info("FileHandlerServiceImpl:: downloadFileByJcrId id:{}", id);
    Session session = getSession(repository);

    DocumentInfo documentInfo =
        documentInfoService
            .findByJcrId(id, Boolean.FALSE)
            .orElseThrow(
                () ->
                    new ServiceException(
                        ErrorCode.FILE_DOES_NOT_EXIST, ErrorMessages.FILE_DOES_NOT_EXIST));

    FileResponse fileContents;
    try {
      fileContents =
          RepositoryHelper.getFileContents(
              session, JCRUtil.generatePathWithVersion(JCRUtil.generateBasePath(documentInfo.getBasePath()),
                          documentInfo.getVersion()), documentInfo.getFileName());
    } catch (ServiceException e) {
      throw new ServiceException(e.getCode(), e.getMessage());
    } catch (IOException | RepositoryException e) {
      throw new ServiceException(ErrorCode.FILE_DOWNLOAD, ErrorMessages.FILE_DOWNLOAD);
    }

    sessionSave(session);

    sessionLogout(session);
    return fileContents.getBytes();
  }

  @Override
  public UploadResponseDto uploadFile(
      MigrationUploadRequestDto migrationUploadRequestDto, MultipartFile file) {
    log.info("FileHandlerServiceImpl:: uploadFile ");

    String basePath = JCRUtil.generateBasePath(migrationUploadRequestDto.getBasePath());
    String fileName = migrationUploadRequestDto.getFileName();

    Optional<DocumentInfo> optionalDoc = documentInfoService.findLatestDocByBasePathAndFileName(migrationUploadRequestDto.getBasePath(), migrationUploadRequestDto.getFileName());
    Long version = JCRUtil.getVersion(optionalDoc);


    log.info("BasePath: {},File name : {}", basePath, fileName);
    File newFile = null;
    Long totalSpace;

    // Create a JCR session
    Session session = getSession(repository);
    DocumentInfo documentInfo = documentInfoService.saveDocumentInfo(migrationUploadRequestDto, version);

    try {
      newFile = convertMultipartFileToFile(fileName, file.getBytes());
      totalSpace = newFile.getTotalSpace();
      RepositoryHelper.addFileNode(session, JCRUtil.generatePathWithVersion(basePath, version), newFile, JackrabbitConstants.USER);
    } catch (ServiceException e) {
      documentInfoService.deleteById(documentInfo.getId());
      throw new ServiceException(e.getCode(), e.getMessage());
    } catch (RepositoryException | IOException e) {
      documentInfoService.deleteById(documentInfo.getId());
      throw new ServiceException(
          ErrorCode.FILE_UPLOAD, ErrorMessages.FILE_UPLOAD + ": " + e.getMessage());
    } finally {
      newFile.delete();
      log.info("Local file deleted successfully.");
    }

    sessionSave(session);
    sessionLogout(session);

    return UploadResponseDto.builder()
        .jcrId(documentInfo.getJcrId())
        .revId(documentInfo.getRevisionId())
        .fileName(documentInfo.getFileName())
        .size(String.valueOf(totalSpace))
        .revision(documentInfo.getRevisionName())
        .build();
  }

  private void sessionSave(Session session) {
    try {
      session.save();
    } catch (RepositoryException e) {
      throw new ServiceException(123, "123");
    }
    System.out.println("Session Save");
  }

  private File convertMultipartFileToFile(String fileName, byte[] fileByteArray) {
    File newFile = new File(fileName);

    try (OutputStream os = new FileOutputStream(newFile)) {
      os.write(fileByteArray);
    } catch (IOException e) {
      log.error("convertMultipartFileToFile :: Error occurred : {}", e.getMessage());
      throw new ServiceException(
          ErrorCode.ERROR_OCCURRED_MULTIPART_FILE, ErrorMessages.ERROR_OCCURRED_MULTIPART_FILE);
    }
    return newFile;
  }

  private void sessionLogout(Session session) {
    session.logout();
    System.out.println("Session Logout");
  }

  private Session getSession(Repository repo) {
    try {
      return repo.login(
          new SimpleCredentials(
              JackrabbitConstants.USER, JackrabbitConstants.PASSWORD.toCharArray()));
    } catch (RepositoryException e) {
      throw new ServiceException(
          ErrorCode.ERROR_OCCURRED_SESSION, ErrorMessages.ERROR_OCCURRED_SESSION);
    }
  }
}
