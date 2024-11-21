package com.epam.edp.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachBookDto {

    private int id;
    private String clientEmail;
    private String coachEmail;
    private String date;
    private Integer duration;
    private String workoutType;
    private String status;

}
