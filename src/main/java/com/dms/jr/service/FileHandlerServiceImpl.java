package com.dms.jr.service;

import com.dms.jr.bean.FileResponse;
import com.dms.jr.exceptions.ServiceException;
import com.dms.jr.helper.RepositoryHelper;
import com.dms.jr.utils.ErrorCode;
import com.dms.jr.utils.ErrorMessages;
import com.dms.jr.utils.JackrabbitConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
@Slf4j
public class FileHandlerServiceImpl implements FileHandlerService {
    private final Repository repository;

    public FileHandlerServiceImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void uploadFile(String basePath, String fileName, MultipartFile file) {

        Repository repo = repository;

        // Create a JCR session
        Session session = getSession(repo);

        try {
            File newFile = convertMultipartFileToFile(fileName, file.getBytes());
            RepositoryHelper.addFileNode(session, basePath, newFile, JackrabbitConstants.USER);
        } catch (ServiceException e) {
            throw new ServiceException(e.getCode(), e.getMessage());
        } catch (RepositoryException | IOException e) {
            throw new ServiceException(ErrorCode.FILE_UPLOAD, ErrorMessages.FILE_UPLOAD + ": " + e.getMessage());
        }

        sessionSave(session);
        sessionLogout(session);
    }

    @Override
    public Resource downloadFile(String basePath, String fileName) {
        Repository repo = repository;
        Session session = getSession(repo);


        FileResponse fileContents = null;
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
    public void deleteFile(String basePath, String fileName) {
        Repository repo = repository;
        Session session = getSession(repo);

        try {
            RepositoryHelper.removeFileContents(session, basePath, fileName);
        } catch (ServiceException e) {
            throw new ServiceException(e.getCode(), e.getMessage());
        } catch (RepositoryException e) {
            throw new ServiceException(ErrorCode.FILE_DELETE_ERROR, ErrorMessages.FILE_DELETE_ERROR);
        }
        sessionSave(session);
        sessionLogout(session);
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
            throw new ServiceException(123, "123");
        }
        return newFile;
    }

    private void sessionLogout(Session session) {
        session.logout();
        System.out.println("Session Logout");
    }

    private Session getSession(Repository repo) {
        try {
            return repo.login(new SimpleCredentials(JackrabbitConstants.USER, JackrabbitConstants.PASSWORD.toCharArray()));
        } catch (RepositoryException e) {
            throw new ServiceException(ErrorCode.ERROR_OCCURRED_SESSION, ErrorMessages.ERROR_OCCURRED_SESSION);
        }
    }

}
