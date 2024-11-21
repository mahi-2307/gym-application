package com.epam.edp.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoachFeedbackResponseDto {

    private Integer feedback_id;
    private Integer booking_id;
    private String feedback;

}