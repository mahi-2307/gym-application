package com.epam.edp.demo.service;


import com.epam.edp.demo.dto.request.UserRequestDto;

public interface SignupService {
    UserRequestDto signup(UserRequestDto signupDto);
    boolean checkIfUserExists(String email);
}
