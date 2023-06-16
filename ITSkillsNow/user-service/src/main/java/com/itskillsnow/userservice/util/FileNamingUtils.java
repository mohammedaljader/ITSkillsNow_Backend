package com.itskillsnow.userservice.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileNamingUtils {
    public static String getOriginalFilename(String newFilename) {
        String originalFilenameWithExtension = newFilename.substring(newFilename.indexOf("_") + 1);
        String originalBasename = getFileBaseName(originalFilenameWithExtension);
        String originalExtension = getFileExtension(originalFilenameWithExtension);
        String filename = originalBasename + "." + originalExtension;
        return URLDecoder.decode(filename, StandardCharsets.UTF_8);
    }

    public static String getBlobFilename(String blobUrl) {
        URI uri = URI.create(blobUrl);
        String path = uri.getPath();
        return Paths.get(path).getFileName().toString();
    }

    public static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    public static String getFileBaseName(String filename) {
        String basename = Paths.get(filename).getFileName().toString();
        int dotIndex = basename.lastIndexOf('.');
        return (dotIndex == -1) ? basename : basename.substring(0, dotIndex);
    }
}
