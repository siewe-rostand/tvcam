package com.siewe_rostand.tvcam.Users.dto;

import lombok.*;

import java.util.Set;

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
    private String fullname;
    private String telephone;
    private String telephoneAlt;
    private Set<String> role;
    private String address;
    private String created_at;
    private boolean active;
}
