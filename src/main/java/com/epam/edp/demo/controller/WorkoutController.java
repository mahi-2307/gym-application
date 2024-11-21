package com.epam.edp.demo.controller;

import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.dto.request.FeedbackRequestDto;
import com.epam.edp.demo.dto.response.FeedbackResponseDto;
import com.epam.edp.demo.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("workout")
@CrossOrigin(origins = "*")
public class WorkoutController {

    @Autowired
    private CoachBookServiceImpl coachBookService;
    @Autowired
    private ClientWorkoutServiceImpl clientWorkoutService;
    @Autowired
    private FinishWorkoutServiceImpl finishWorkoutService;
    @Autowired
    private CancelSessionServiceImpl cancelSessionService;
    @Autowired
    ClientFeedbackServiceImpl feedbackService;

    @PostMapping("/bookCoach")
    @Operation(summary = "Book Coach", security = {@SecurityRequirement(name = "bearerAuth")})

    public ResponseEntity<CoachBookDto> bookCoach(
            @RequestBody CoachBookDto coachBookDto,
            @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {

        CoachBookDto response = coachBookService.bookCoach(coachBookDto, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getClientWorkouts")
    @Operation(summary = "Get Client workout", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<List<CoachBookDto>> getClientWorkouts(@RequestHeader("Authorization") String authorizationHeader) throws UsernameNotFoundException {
        List<CoachBookDto> response = clientWorkoutService.getClientWorkouts(authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/finishWorkout")
    @Operation(summary = "Finish Workout", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> finishWorkout(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authorizationHeader) {
        Integer id = requestBody.get("id");
        if (id != null) {
            finishWorkoutService.finishWorkout(id, authorizationHeader);
            return ResponseEntity.ok("Workout finished successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid request: 'id' is required");
        }
    }

    @PutMapping("/cancelSession")
    @Operation(summary = "Cancel Workout", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> cancelSession(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authorizationHeader) {
        Integer id = requestBody.get("id");
        if (id != null) {
            cancelSessionService.cancelSession(id, authorizationHeader);
            return ResponseEntity.ok("Workout cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid request: 'id' is required");
        }
    }

    @PostMapping("/client-feedback")
    @Operation(summary = "Client feedback", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<FeedbackResponseDto> saveClientFeedback(@RequestBody FeedbackRequestDto feedback, @RequestHeader("Authorization") String Authorization) throws JsonProcessingException {
        String token = Authorization.replace("Bearer","");
        return new ResponseEntity<>(feedbackService.saveClientFeedback(feedback,token), HttpStatus.OK);
    }
}
