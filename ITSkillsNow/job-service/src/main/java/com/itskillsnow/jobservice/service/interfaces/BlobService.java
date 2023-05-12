package com.itskillsnow.jobservice.service.interfaces;


import java.io.InputStream;
import java.util.List;

public interface BlobService {
    String downloadFile(String blobItem);
    String storeFile(String filename, InputStream content, long length);
    void deleteFile(String filename);
}
