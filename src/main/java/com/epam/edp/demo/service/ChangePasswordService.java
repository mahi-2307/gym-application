package com.epam.edp.demo.service;

import com.epam.edp.demo.dto.request.ChangePasswordDto;

public interface ChangePasswordService {
    void changePassword(String token, ChangePasswordDto changePasswordDto);
}
