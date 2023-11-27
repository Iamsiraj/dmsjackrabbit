package com.dms.jr.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENT_INFO")
@Getter
@Setter
@ToString
public class DocumentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "BASE_PATH")
    private String basePath;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "DOCUMENT_ID")
    private String documentId;

    @Column(name = "REVISION_ID")
    private String revisionId;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "REVISION_NAME")
    private String revisionName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IS_DELETED")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @PrePersist
    public void prePersist() {
        this.documentId = "{jcrUuid}" + UUID.randomUUID();
        this.revisionId = "{jcrRev}" + UUID.randomUUID();
        this.revisionName = "1.0";
    }
}
