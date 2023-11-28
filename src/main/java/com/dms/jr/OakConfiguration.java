package com.dms.jr;

import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.jcr.Repository;
import javax.sql.DataSource;

@Configuration
@Slf4j
public class OakConfiguration {

  private volatile Repository repository;

  private final DataSource dataSource;

  public OakConfiguration(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Bean
  public Repository repository() {

    DocumentNodeStore store =
        RDBDocumentNodeStoreBuilder.newRDBDocumentNodeStoreBuilder()
            .setRDBConnection(dataSource)
            .build();

    repository = new Jcr(new Oak(store)).createRepository();
    log.info("Initializing OAK Repository ....");
    return repository;
  }

  @PreDestroy
  public void destroy() {
    if (repository instanceof JackrabbitRepository) {
      log.info("Shutting Down OAK Repository ....");
      ((JackrabbitRepository) repository).shutdown();
    }
  }
}
