package com.epam.edp.demo.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequestDto {
    private Integer booking_id;
    private String notes;
    private Integer rating;
}
