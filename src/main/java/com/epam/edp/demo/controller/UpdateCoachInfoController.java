package com.epam.edp.demo.controller;


import com.epam.edp.demo.dto.request.CoachEditProfilePicDto;
import com.epam.edp.demo.dto.request.CoachUpdateRequestDto;
import com.epam.edp.demo.service.UploadProfilePictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("coachprofile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UpdateCoachInfoController {

    private final UploadProfilePictureService uploadProfilePictureService;

    @PutMapping("/update")
    @Operation(summary = "Update coach Information", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "about",required = false) String about,
            @RequestParam(value = "specialization",required = false) List<String> specialization,
            @RequestParam(value = "profileImage",required = false) MultipartFile profileImage,
            @RequestParam(value = "pdfDocument",required = false) MultipartFile pdfDocument) {

        CoachEditProfilePicDto requestData = new CoachEditProfilePicDto();
        requestData.setName(name);
        requestData.setTitle(title);
        requestData.setAbout(about);
        requestData.setSpecialization(specialization);
        requestData.setProfilePicture(profileImage);
        requestData.setCertificate(pdfDocument);


        return uploadProfilePictureService.updateProfile(authorizationHeader, requestData);
    }
}

