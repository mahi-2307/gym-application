package com.epam.edp.demo.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponseDto {
    private Integer feedback_id;
    private Integer booking_id;
    private String notes;
    private Integer rating;
    private String createdAt;
}
