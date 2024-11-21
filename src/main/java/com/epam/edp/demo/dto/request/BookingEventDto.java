package com.epam.edp.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEventDto {
    private String coachEmail;
    private int sessionDuration;
    private String clientEmail;

}
