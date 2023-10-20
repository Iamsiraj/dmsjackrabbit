package com.dms.jr;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.jcr.Repository;
import java.io.File;
import java.io.IOException;

@Configuration
public class OakConfiguration {

    private static Logger log = LoggerFactory.getLogger(OakConfiguration.class);

    private volatile Repository repository;


    @Bean
    public Repository repository() {
        try {
            final FileStore fs = FileStoreBuilder.fileStoreBuilder(new File("repository")).build();
            final SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            repository = new Jcr(new Oak(ns)).createRepository();
            log.info("Reository creaetd with nodeStore, {}", ns);
        } catch (InvalidFileStoreVersionException e) {
            log.error("Failed to create repository due to invalid file store version.", e);
        } catch (IOException e) {
            log.error("Failed to create repository due to IO exception.", e);
        }

        return repository;
    }

    @PreDestroy
    public void destroy() {
        if (repository instanceof JackrabbitRepository) {
            ((JackrabbitRepository) repository).shutdown();
        }
    }
}