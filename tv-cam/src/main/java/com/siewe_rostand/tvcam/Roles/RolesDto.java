package com.siewe_rostand.tvcam.Roles;

public class RolesDto {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RolesDto CreateDTO(Roles roles){
        RolesDto rolesDto = new RolesDto();

        if (roles != null){
            rolesDto.setId(roles.getRoleId());
            rolesDto.setName(roles.getName());
        }

        return  rolesDto;
    }
}
