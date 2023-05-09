package com.itskillsnow.courseservice.util;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class FileNamingUtilsTest {
    @Test
    public void testGetOriginalFilename() {
        String newFilename = "8d28f0d5-7b8d-4dcf-a83e-3e7e966dbe23_example_file.txt";
        String originalFilename = FileNamingUtils.getOriginalFilename(newFilename);
        assertEquals("example_file.txt", originalFilename);
    }

    @Test
    public void testGetOriginalFilename2() {
        String newFilename = "8d28f0d5-7b8d-4dcf-a83e-3e7e966dbe23_DevOps%20Document.docx";
        String originalFilename = FileNamingUtils.getOriginalFilename(newFilename);
        assertEquals("DevOps Document.docx", originalFilename);
    }

    @Test
    public void testGetBlobFilename() {
        String blobUrl = "https://example.blob.core.windows.net/container-name/blob-name.txt";
        String expectedFilename = "blob-name.txt";
        String actualFilename = FileNamingUtils.getBlobFilename(blobUrl);
        assertEquals(expectedFilename, actualFilename);
    }

    @Test
    public void testGetFileExtension() {
        String filename = "example_file.txt";
        String extension = FileNamingUtils.getFileExtension(filename);
        assertEquals("txt", extension);
    }

    @Test
    public void testGetFileExtensionEmptyString() {
        String filename = "file_without_extension";
        String extension = FileNamingUtils.getFileExtension(filename);
        assertEquals("", extension);
    }

    @Test
    public void testGetFileBaseName() {
        String filename = "example_file.txt";
        String basename = FileNamingUtils.getFileBaseName(filename);
        assertEquals("example_file", basename);

        filename = "example_file";
        basename = FileNamingUtils.getFileBaseName(filename);
        assertEquals("example_file", basename);

        filename = ".hidden_file";
        basename = FileNamingUtils.getFileBaseName(filename);
        assertEquals("", basename);
    }

}