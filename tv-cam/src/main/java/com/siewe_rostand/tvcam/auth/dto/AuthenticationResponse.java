package com.siewe_rostand.tvcam.auth.dto;

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
public class AuthenticationResponse {

    private String tokenType;
    private String accessToken;
    private Long expiresIn;

}
