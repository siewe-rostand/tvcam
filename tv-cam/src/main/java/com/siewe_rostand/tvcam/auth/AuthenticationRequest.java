package com.siewe_rostand.tvcam.auth;

import lombok.*;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {

    private String telephone;
    private String password;
}
