package com.epam.edp.demo.dto.request;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachEditProfilePicDto {

    private String name;
    private String title;
    private String about;
    private List<String> specialization;
    private MultipartFile profilePicture;
    private MultipartFile certificate;
}
