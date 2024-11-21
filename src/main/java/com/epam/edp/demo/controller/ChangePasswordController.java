package com.epam.edp.demo.controller;

import com.epam.edp.demo.dto.request.ChangePasswordDto;
import com.epam.edp.demo.dto.response.ChangePasswordResponseDto;
import com.epam.edp.demo.service.ChangePasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChangePasswordController {
    private final ChangePasswordService changePasswordService;

    @PutMapping("/password")
    @Operation(summary = "Change Password", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ChangePasswordResponseDto> changePassword(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ChangePasswordDto changePasswordDto) {

        String token = authorizationHeader.replace("Bearer ", "");
        changePasswordService.changePassword(token, changePasswordDto);
        return ResponseEntity.ok(new ChangePasswordResponseDto("Password updated successfully"));
    }
}
