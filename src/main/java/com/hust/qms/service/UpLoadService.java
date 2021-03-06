package com.hust.qms.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hust.qms.dto.UpLoadFileDTO;
import com.hust.qms.exception.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UpLoadService {

    @Autowired
    private Cloudinary cloudinary;

    public ServiceResponse uploadImage(UpLoadFileDTO upLoadFileDTO) {
           try {
               Map response = cloudinary.uploader().upload(upLoadFileDTO.getFile().getBytes(),
                       ObjectUtils.asMap("folder" , "PROJECT3",
                                        "resource_type", "auto"));
               UpLoadFileDTO fileDTO = UpLoadFileDTO.builder()
                       .fileName(response.get("public_id").toString())
                       .sourceUrl(response.get("secure_url").toString())
                       .format(response.get("format").toString())
                       .resourceType(response.get("resource_type").toString())
                       .build();

               return ServiceResponse.SUCCESS(fileDTO);
           }catch (Exception e) {
               e.printStackTrace();
           }
           return ServiceResponse.BAD_RESPONSE("Up file failed");
    }
}
