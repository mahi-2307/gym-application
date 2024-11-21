package com.epam.edp.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CertificateStorageService  {
    String uploadPdfToS3AndDynamoDB(MultipartFile file,String token) throws IOException;
    String getFileKeyById(long id);

}
