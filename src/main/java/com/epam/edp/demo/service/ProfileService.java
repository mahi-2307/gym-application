package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.UpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<?> updateProfile(String authorizationHeader, UpdateRequestDto requestData);

}
