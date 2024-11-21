package com.epam.edp.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRequestDto {
    @NotNull(message = "Full name is required")
    @Size(min = 1, max = 100, message = "Full name must be between 1 and 100 characters")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+)*$", message = "Full name can only contain letters and spaces")
    private String fullName;

    @NotNull(message = "Target is required")
    @Size(max = 200, message = "Target cannot exceed 200 characters")
    private String target;

    @NotNull(message = "Preferable activity is required")
    @Size(max = 100, message = "Preferable activity cannot exceed 100 characters")
    private String preferableActivity;
}
