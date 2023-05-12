package com.itskillsnow.jobservice.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.itskillsnow.jobservice.exception.BlobServiceException;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.util.UUID;

import static com.itskillsnow.jobservice.util.FileNamingUtils.getFileBaseName;
import static com.itskillsnow.jobservice.util.FileNamingUtils.getFileExtension;

@Service
@Slf4j
public class BlobServiceImpl implements BlobService {
    private final BlobContainerClient containerClient;

    public BlobServiceImpl(
            @Value("${spring.cloud.azure.storage.blob.connection-string}") String connectionString,
            @Value("${spring.cloud.azure.storage.blob.account-name}") String containerName
    ) {
        this.containerClient = new BlobServiceClientBuilder()
                .connectionString(connectionString).buildClient()
                .getBlobContainerClient(containerName);
    }

    @Override
    public String downloadFile(String blobItem) {
        BlobClient blobClient = containerClient.getBlobClient(blobItem);
        return blobClient.getBlobUrl();
    }
    @Override
    public String storeFile(String filename, InputStream content, long length) {
        // Generate unique filename
        String newFilename = UUID.randomUUID().toString()
                .concat("_")
                .concat(getFileBaseName(filename))
                .concat(".")
                .concat(getFileExtension(filename));

        BlobClient blobClient = containerClient.getBlobClient(newFilename);
        try (content) {
            blobClient.upload(content, length);
            return blobClient.getBlobUrl();
        } catch (Exception e) {
            throw new BlobServiceException("Error uploading file to Azure Blob Storage", e);
        }
    }

    @Override
    public void deleteFile(String filename) {
        BlobClient blobClient = containerClient.getBlobClient(filename);
        blobClient.deleteIfExists();
    }
}
