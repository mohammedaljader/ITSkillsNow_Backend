package com.itskillsnow.userservice.service.interfaces;

import java.io.InputStream;

public interface BlobService {
    String downloadFile(String blobItem);
    String storeFile(String filename, InputStream content, long length);
    void deleteFile(String filename);
}
