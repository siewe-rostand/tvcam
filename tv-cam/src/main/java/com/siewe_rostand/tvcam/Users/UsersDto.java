package com.siewe_rostand.tvcam.Users;

import com.siewe_rostand.tvcam.Roles.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
public class UsersDto {

    private Long id;

    private String firstname;

    private String lastname;

    private String telephone;


    private String address;

    private Boolean activated;

    private Collection<String> roles;

    public UsersDto CreateDTO(Users users){
        UsersDto usersDto = new UsersDto();

        if (users != null){
            usersDto.setId(users.getUserId());
            usersDto.setFirstname(users.getFirstname());
            usersDto.setLastname(users.getLastname());
            usersDto.setTelephone(users.getTelephone());
            usersDto.setAddress(users.getAddress());
            usersDto.setActivated(users.getActive());
            /*
              make sure the role table in the database contain some roles
             */
            HashSet<String> roles = new HashSet<>();
            if (users.getRoles().size() > 0){
                for (Roles role : users.getRoles()){
                    roles.add(role.getName());
                }
            }
            usersDto.setRoles(roles);
        }
        return  usersDto;
    }
}
