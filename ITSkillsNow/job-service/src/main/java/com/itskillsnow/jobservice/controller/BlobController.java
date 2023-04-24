package com.itskillsnow.jobservice.controller;

import com.itskillsnow.jobservice.dto.request.AddFileDto;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public String uploadFile(@RequestBody AddFileDto addFileDto) throws IOException {
        log.info("Filename :" + addFileDto.getFile().getOriginalFilename());
        log.info("Size:" +  addFileDto.getFile().getSize());
        log.info("ContentType:" +  addFileDto.getFile().getContentType());
        Boolean storeFile = service.storeFile(addFileDto.getFile().getOriginalFilename(),
                addFileDto.getFile().getInputStream(), addFileDto.getFile().getSize());
        if(storeFile){
            return addFileDto.getFile().getOriginalFilename() + " Has been saved as a blob-item!!!";
        }else{
            return "The file " + addFileDto.getFile().getOriginalFilename() + " already exists in Azure Blob Storage";
        }
    }
}