package com.epam.edp.demo.controller;


import com.epam.edp.demo.config.RabbitMQConsumer;
import com.epam.edp.demo.config.RabbitMQProducer;
import com.epam.edp.demo.dto.request.SignInRequestDto;
import com.epam.edp.demo.dto.request.UserRequestDto;
import com.epam.edp.demo.service.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final SignInServiceImpl signInService;
    private final SignupService signupService;
    private final RabbitMQProducer rabbitMQProducer;
    private final RabbitMQConsumer rabbitMQConsumer;
    @GetMapping("publish")
    public String publishMessage(@RequestParam("message") String message){
        rabbitMQProducer.sendBookingMessage(message);
        return "Message Published";
    }
//    @GetMapping("getMessage")
//    public String getMessage(){
//        return rabbitMQConsumer.receiveMessage();
//    }

    @PostMapping("signup")
    @Operation(summary = "SignUp",security = {} )
    public ResponseEntity<UserRequestDto> signup(@RequestBody UserRequestDto signupDto){
        return new ResponseEntity<>(signupService.signup(signupDto  ), HttpStatus.CREATED);
    }
    @PostMapping("login")
    @Operation(summary = "Login",security = {} )
    public ResponseEntity<?> login(@RequestBody SignInRequestDto signInDto){
       return new ResponseEntity<>(signInService.signIn(signInDto),HttpStatus.OK);
    }
}
