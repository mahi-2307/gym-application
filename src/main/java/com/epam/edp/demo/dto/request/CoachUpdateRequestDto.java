package com.epam.edp.demo.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachUpdateRequestDto {
    private String name;
    private String title;
    private String about;
    private List<String> specialization;
}
