package com.epam.edp.demo.controller;


import com.epam.edp.demo.dto.request.UpdateRequestDto;
import com.epam.edp.demo.service.ProfileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    ProfileServiceImpl updateProfileService;

    @PutMapping("/update")
    @Operation(summary = "Update profile", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authorizationHeader,
                                           @Valid @RequestBody UpdateRequestDto requestData) {
        return updateProfileService.updateProfile(authorizationHeader, requestData);
    }
    @GetMapping("/userDetails")
    @Operation(summary = "Get user details", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        return updateProfileService.getProfile(authorizationHeader);
    }


}
