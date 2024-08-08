package com.siewe_rostand.tvcam.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siewe_rostand.tvcam.Roles.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users implements UserDetails {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySequence")
    @SequenceGenerator(name = "mySequence", sequenceName = "MY_SEQ", allocationSize = 1)
    private Long userId;


    private UUID uid;

    private String firstname;

    private String lastname;

    @JsonIgnore
    private String password;

    @Min(9)
    @Column(unique = true)
    private String telephone;

    private String address;

    private String imageUrl;


    private Boolean active;


    private String createdAt;


    private String updatedAt;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Roles> roles;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    @PrePersist
    protected  void PrePersist(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        String formattedDate = formatter.format(now);
        if(createdAt == null) createdAt = formattedDate;
        if (this.updatedAt == null) updatedAt = formattedDate;
        uid = UUID.randomUUID();
    }

    @PreUpdate
    protected  void PreUpdate(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        updatedAt = formatter.format(now);
    }
    @Override
    public String getUsername() {
        return telephone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
