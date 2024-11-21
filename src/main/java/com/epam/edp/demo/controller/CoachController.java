package com.epam.edp.demo.controller;

import com.epam.edp.demo.dto.request.CoachBookDto;
import com.epam.edp.demo.dto.request.CoachFeedbackDto;
import com.epam.edp.demo.dto.response.CoachFeedbackResponseDto;
import com.epam.edp.demo.dto.response.CoachResponseDto;
import com.epam.edp.demo.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoachController {

    private final CoachService coachesService;
    @Autowired
    private CancelCoachWorkoutServiceImpl cancelCoachWorkoutService;
    @Autowired
    private CoachFeedbackServiceImpl coachFeedbackService;

    @Autowired
    private CoachWorkoutScheduleServiceImpl coachWorkoutScheduleService;
    @Autowired
    private CoachAllFeedbackServiceImpl coachAllFeedbackService;
    @GetMapping
    @Operation(summary = "Get All Coaches", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<List<CoachResponseDto>> getAllCoaches(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ", "");
        return new ResponseEntity<List<CoachResponseDto>>(coachesService.getCoaches(token), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get Coach By Id", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<CoachResponseDto> getCoachById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id){
        String token = authorizationHeader.replace("Bearer ", "");
        return new ResponseEntity<CoachResponseDto>(coachesService.getCoachById(token,id), HttpStatus.OK);
    }
    @GetMapping("/workouts")
    @Operation(summary = "Get Coach workouts schedule", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<List<CoachBookDto>> getCoachWorkoutSchedule(@RequestHeader("Authorization") String authorizationHeader) throws UsernameNotFoundException {
        List<CoachBookDto> response = coachWorkoutScheduleService.getCoachWorkoutSchedule(authorizationHeader);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/cancel")
    @Operation(summary = "Cancel Workout", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> cancelSession(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authorizationHeader) {
        Integer id = requestBody.get("id");
        if (id != null) {
            cancelCoachWorkoutService.cancelCoachWorkout(id, authorizationHeader);
            return ResponseEntity.ok("Workout cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid request: 'id' is required");
        }
    }
    @GetMapping("/feedbacks")
    @Operation(summary = "Coach feedback to the workout", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<List<Map<String, Object>>> getCoachFeedbacks(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "6") Integer pageSize) {

        List<Map<String, Object>> feedbackPage = coachAllFeedbackService.getCoachAllFeedbacks(authorizationHeader, pageNumber, pageSize);
        return ResponseEntity.ok(feedbackPage);
    }

    @PostMapping("/coachFeedback")
    @Operation(summary = "Coach feedback", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<CoachFeedbackResponseDto> saveCoachFeedback(@RequestBody CoachFeedbackDto feedback, @RequestHeader("Authorization") String authorizationHeader) {
        return new ResponseEntity<>(coachFeedbackService.saveCoachFeedback(feedback,authorizationHeader), HttpStatus.OK);
    }
}
