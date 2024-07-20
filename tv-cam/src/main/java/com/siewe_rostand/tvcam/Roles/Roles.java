package com.siewe_rostand.tvcam.Roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siewe_rostand.tvcam.Users.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long roleId;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<Users> users;

    @Column(unique = true)
    private String name;

}
