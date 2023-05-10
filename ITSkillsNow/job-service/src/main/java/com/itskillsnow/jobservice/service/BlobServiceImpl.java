package com.itskillsnow.jobservice.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.itskillsnow.jobservice.exception.BlobServiceException;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<String> getAllFiles() {
        return containerClient.listBlobs()
                .stream()
                .map(BlobItem::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String downloadFile(String blobItem) {
        BlobClient blobClient = containerClient.getBlobClient(blobItem);
        return blobClient.getBlobUrl();
    }
    @Override
    public Boolean storeFile(String filename, InputStream content, long length) {
        // Generate unique filename
        String newFilename = UUID.randomUUID().toString()
                .concat("_")
                .concat(getFileBaseName(filename))
                .concat(".")
                .concat(getFileExtension(filename));

        BlobClient blobClient = containerClient.getBlobClient(newFilename);
        try (content) {
            blobClient.upload(content, length);
            log.info(blobClient.getBlobUrl());
            return true;
        } catch (Exception e) {
            throw new BlobServiceException("Error uploading file to Azure Blob Storage", e);
        }
    }

    @Override
    public boolean deleteFile(String filename) {
        BlobClient blobClient = containerClient.getBlobClient(filename);
        return blobClient.deleteIfExists();
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    private String getFileBaseName(String filename) {
        String basename = Paths.get(filename).getFileName().toString();
        int dotIndex = basename.lastIndexOf('.');
        return (dotIndex == -1) ? basename : basename.substring(0, dotIndex);
    }
}
