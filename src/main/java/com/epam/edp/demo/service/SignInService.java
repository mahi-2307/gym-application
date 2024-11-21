package com.epam.edp.demo.service;


import com.epam.edp.demo.dto.request.SignInRequestDto;
import com.epam.edp.demo.dto.response.SignInResponseDto;

public interface SignInService {
    SignInResponseDto signIn(SignInRequestDto signInDto);
}
