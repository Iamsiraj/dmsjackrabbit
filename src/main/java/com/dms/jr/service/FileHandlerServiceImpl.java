package com.dms.jr.service;

import com.dms.jr.bean.FileResponse;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
import org.springframework.beans.factory.annotation.Value;
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
public class FileHandlerServiceImpl implements FileHandlerService {

    @Value("${spring.datasource.url}")
    private String DATASOURCE_URL;
    @Value("${spring.datasource.username}")
    private String DATASOURCE_USERNAME;

    @Value("${spring.datasource.password}")
    private String DATASOURCE_PASSWORD;

    @Override
    public void uploadFile(String basePath, String fileName, MultipartFile file) throws RepositoryException {

        Repository repo = getOrCreateRepository();

        // Create a JCR session
        Session session = getSession(repo);

        File newFile = new File(fileName);

        try (OutputStream os = new FileOutputStream(newFile)) {
            os.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//		Saving PDF File
        try {
            RepositoryHelper.addFileNode(session, basePath, newFile, "admin");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        session.save();
        System.out.println("Session Save");

        session.logout();
        System.out.println("Session Logout");
    }

    @Override
    public Resource downloadFile(String basePath, String fileName) throws RepositoryException {
        Repository repo = getOrCreateRepository();
        Session session = getSession(repo);


        FileResponse fileContents = null;
        try {
            fileContents = RepositoryHelper.getFileContents(session, basePath, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File newFile = new File(fileName);

        try (OutputStream os = new FileOutputStream(newFile)) {
            os.write(fileContents.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Resource resource = new FileSystemResource(newFile);


        session.save();
        System.out.println("Session Save");

        session.logout();
        System.out.println("Session Logout");

        return resource;
    }

    private Session getSession(Repository repo) throws RepositoryException {
        return repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }

    private Repository getOrCreateRepository() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(DATASOURCE_URL);
        dataSource.setUsername(DATASOURCE_USERNAME);
        dataSource.setPassword(DATASOURCE_PASSWORD);

        DocumentNodeStore store = RDBDocumentNodeStoreBuilder.newRDBDocumentNodeStoreBuilder()
                .setRDBConnection(dataSource).build();

        return new Jcr(new Oak(store)).createRepository();
    }
}
