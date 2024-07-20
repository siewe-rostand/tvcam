package com.siewe_rostand.tvcam.Users;

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
public class UserRequest {

    private Long id;
    @NotNull(message = "First name is mandatory")
    private String firstname;
    @NotNull(message = "Last name is mandatory")
    private String lastname;
    @NotNull(message = "Telephone number is mandatory")
    private String telephone;
    @NotNull(message = "Email name is mandatory")
    @Size(min = 6, max = 16, message = "Password should be between 6 and 16 chars")
    private String password;
    private String address;
}
