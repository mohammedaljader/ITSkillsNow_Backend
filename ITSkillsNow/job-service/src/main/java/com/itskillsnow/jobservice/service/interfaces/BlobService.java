package com.itskillsnow.jobservice.service.interfaces;


import java.io.InputStream;
import java.util.List;

public interface BlobService {
    List<String> getAllFiles();
    String downloadFile(String blobItem);
    Boolean storeFile(String filename, InputStream content, long length);
    boolean deleteFile(String filename);
}
