package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.CoachEditProfilePicDto;
import com.epam.edp.demo.dto.request.CoachUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UploadProfilePictureService {
    ResponseEntity<?> updateProfile(String authorizationHeader, CoachEditProfilePicDto requestData);
}
