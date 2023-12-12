package com.dms.jr.util;

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
}
