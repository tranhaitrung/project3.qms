package com.hust.qms.controller;

import com.cloudinary.Cloudinary;
import com.hust.qms.dto.UpLoadFileDTO;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.UpLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(value = "*", maxAge = 3600)
public class UploadController {
    @Autowired
    private UpLoadService upLoadService;

    @PostMapping(value = "/up-file")
    public ResponseEntity upImage(@ModelAttribute UpLoadFileDTO upLoadFileDTO) {
//        UpLoadFileDTO upLoadFileDTO = new UpLoadFileDTO();
//        upLoadFileDTO.setFile(file);
        ServiceResponse response = upLoadService.uploadImage(upLoadFileDTO);
        return new ResponseEntity(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
