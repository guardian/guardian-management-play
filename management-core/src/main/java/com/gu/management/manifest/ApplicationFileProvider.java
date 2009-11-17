package com.gu.management.manifest;

import java.util.List;

public interface ApplicationFileProvider {
    List<String> getFileContents(String manifestFilePath);

    String getAbsolutePath(String manifestFilePath);
}
