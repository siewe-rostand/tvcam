package com.siewe_rostand.tvcam.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotNull(message = "First name is mandatory")
    @NotBlank(message = "First name is mandatory")
    private String firstname;

    @NotNull(message = "Last name is mandatory")
    @NotBlank(message = "Last name is mandatory")
    private String lastname;

    @NotNull(message = "Telephone number is mandatory")
    @NotBlank(message = "Telephone number is mandatory")
    @Size(min = 9, max = 9, message = "Telephone number must be exactly 9 digits")
    private String telephone;
    
    @NotNull(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 16, message = "Password should be between 6 and 16 chars")
    private String password;
}
