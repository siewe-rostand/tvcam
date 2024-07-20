package com.siewe_rostand.tvcam.Users;

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
public class UserResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String telephone;
    private String address;
    private boolean active;
}
