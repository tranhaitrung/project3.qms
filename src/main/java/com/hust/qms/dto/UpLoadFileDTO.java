package com.hust.qms.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UpLoadFileDTO {
    private MultipartFile file;
    private String sourceUrl;
    private String format;
    private String fileName;
    private String resourceType;
}
