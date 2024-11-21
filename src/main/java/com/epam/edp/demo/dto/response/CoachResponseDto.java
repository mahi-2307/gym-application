package com.epam.edp.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachResponseDto {
    private String email;
    private String expertiseDescription;
    private String name;
    private String profilePicture;
    private Double rating;
    private String shortSummary;
}
