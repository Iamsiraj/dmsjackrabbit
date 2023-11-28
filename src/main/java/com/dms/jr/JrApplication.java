package com.dms.jr;

import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.jcr.RepositoryException;
import java.io.IOException;

@SpringBootApplication
public class JrApplication {

  public static void main(String[] args)
      throws InvalidFileStoreVersionException, IOException, RepositoryException {
    SpringApplication.run(JrApplication.class, args);
  }
}
