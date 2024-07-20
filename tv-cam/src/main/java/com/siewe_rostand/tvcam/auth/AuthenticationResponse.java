package com.siewe_rostand.tvcam.auth;

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

    private String token;
    private Long userId;
    private String fullname;
    private String telephone;

}
