package com.epam.edp.demo.controller;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.edp.demo.service.CertificateStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
public class CertificateController {

    @Autowired
    private DynamoDB dynamoDB;

    @Autowired
    private AmazonS3 amazonS3;
    @GetMapping("/pdf/{id}")
    @Operation(summary = "Download Certificate", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<InputStreamResource> downloadPDF(@PathVariable String id,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            long numericId = Long.parseLong(id);  // Convert id from String to long
            Table table = dynamoDB.getTable("tm2-coach-upload-info");
            Item item = table.getItem("id", numericId);  // Use numericId here

            if (item != null) {
                String pdfUrl = item.getString("pdfUrl");
                String bucketName = pdfUrl.split("//")[1].split("/")[0].split("\\.")[0];
                String key = pdfUrl.substring(pdfUrl.indexOf(".com/") + 5);

                S3Object s3Object = amazonS3.getObject(bucketName, key);
                InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + key)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @Autowired
    private CertificateStorageService certificateStorageService;

    @PostMapping("/upload-pdf")
    @Operation(summary = "Upload pdf", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String response = certificateStorageService.uploadPdfToS3AndDynamoDB(file, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload PDF: " + e.getMessage());
        }
    }
}
