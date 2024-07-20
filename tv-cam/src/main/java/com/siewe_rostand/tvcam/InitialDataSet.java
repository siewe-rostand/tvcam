package com.siewe_rostand.tvcam;

import com.siewe_rostand.tvcam.Roles.Roles;
import com.siewe_rostand.tvcam.Roles.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialDataSet {


    private final RolesRepository roleRepository;


    public InitializingBean load() {
//        createRoles();
        return null;
    }

//    private void createRoles() {
//        //List<String> roles = Arrays.asList("ADMIN", "USER", "USER");
//        List<String> roles = Arrays.asList("ADMIN", "USER", "CUSTOMER", "MANAGER");
//        for (String role : roles) {
//            if (roleRepository.findByName(role) == null) {
//                roleRepository.save(new Roles(role));
//            }
//        }
//    }


}
