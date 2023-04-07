package com.itskillsnow.jobservice.controller;

import com.itskillsnow.jobservice.service.interfaces.BlobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/blob")
@RequiredArgsConstructor
@Slf4j
public class BlobController {


    private final BlobService service;


    @GetMapping("/")
    public List<String> blobItems() {
        return service.getAllFiles();
    }


    @GetMapping("/download/{filename}")
    public String download(@PathVariable String filename) {
        return service.downloadFile(filename);
    }

    @DeleteMapping("/{filename}")
    public Boolean delete(@PathVariable String filename) {
        return service.deleteFile(filename);
    }

    @PostMapping("/upload")
    public String uploadFile(MultipartFile file) throws IOException {
        log.info("Filename :" + file.getOriginalFilename());
        log.info("Size:" + file.getSize());
        log.info("ContentType:" + file.getContentType());
        Boolean storeFile = service.storeFile(file.getOriginalFilename(), file.getInputStream(), file.getSize());
        if(storeFile){
            return file.getOriginalFilename() + " Has been saved as a blob-item!!!";
        }else{
            return "The file " + file.getOriginalFilename() + " already exists in Azure Blob Storage";
        }
    }
}