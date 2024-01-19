package com.dms.jr.util;

import com.dms.jr.model.DocumentInfo;

import java.util.Optional;
import java.util.UUID;

public class JCRUtil {

  public static String generateJCRId() {
    return "{jcrUuid}" + UUID.randomUUID();
  }

  public static String generateJCRRevId() {
    return "{jcrRev}" + UUID.randomUUID();
  }

  public static String generateBasePath(String basePath) {
    return "/" + basePath;
  }

  public static Long getVersion(Optional<DocumentInfo> optionalDoc) {
    return optionalDoc.map(documentInfo -> documentInfo.getVersion() + 1).orElse(1L);
  }

  public static String generatePathWithVersion(String basePath, Long version) {
    return basePath + "/v" + version;
  }
}
