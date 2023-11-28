package com.dms.jr.repository;

import com.dms.jr.model.DocumentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentInfoRepository extends JpaRepository<DocumentInfo, Integer> {
  // You can add custom query methods here if needed
}
