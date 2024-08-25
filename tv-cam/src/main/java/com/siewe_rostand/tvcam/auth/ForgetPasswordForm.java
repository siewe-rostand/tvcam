package com.siewe_rostand.tvcam.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordForm {
    @NotEmpty(message = "user telephone cannot be empty")
    @NotNull(message = "Telephone cannot be null")
    @NotBlank(message = "Telephone cannot be blank")
    private String telephone;
    @NotEmpty(message = "New password cannot be empty")
    @NotNull(message = "New password cannot be null")
    @NotBlank(message = "New password cannot be blank")
    private String newPassword;

}
