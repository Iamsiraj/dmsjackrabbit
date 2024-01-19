package com.dms.jr.repository;

import com.dms.jr.model.DocumentInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentInfoRepository extends JpaRepository<DocumentInfo, Integer> {
  Optional<DocumentInfo> findByJcrIdAndIsDeleted(String id, boolean isDeleted);
  
  Optional<DocumentInfo> findFirstByBasePathAndFileNameOrderByVersionDesc(String basePath, String fileName);

}
