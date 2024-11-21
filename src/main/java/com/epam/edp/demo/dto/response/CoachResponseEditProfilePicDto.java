package com.epam.edp.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachResponseEditProfilePicDto {
    private String name;
    private String title;
    private String about;
    private List<String> specialization;
    private String profilePictureUrl;
    private String certificateUrl;
}
