package com.dms.jr;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;

import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.jcr.*;
import java.io.File;
import java.io.IOException;


@SpringBootApplication
public class JrApplication {

	public static void main(String[] args) throws InvalidFileStoreVersionException, IOException, RepositoryException {
		SpringApplication.run(JrApplication.class, args);
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:oracle:thin:@//localhost:1521/oak");
		dataSource.setUsername("system");
		dataSource.setPassword("orcl");

		DocumentNodeStore store = RDBDocumentNodeStoreBuilder.newRDBDocumentNodeStoreBuilder()
				.setRDBConnection(dataSource).build();

		Repository repo = new Jcr(new Oak(store)).createRepository();

		// Create a JCR session
		Session session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
		Node root = session.getRootNode();

		// Store content
		Node hello = root.addNode("hello");
		Node world = hello.addNode("world");
		world.setProperty("message", "Hello, World!");
		session.save();


		session.logout();
	}

}
