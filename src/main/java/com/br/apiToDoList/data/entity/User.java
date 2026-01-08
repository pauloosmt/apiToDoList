package com.br.apiToDoList.data.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.br.apiToDoList.data.dto.request.UserRequestDTO;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(name="name", nullable = false, length = 45)
    private String name;

    @Column(name="email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name="password", nullable = false, length = 100)
    private String password;

    private  UserRole role;
    

    @Builder
    public User(UserRequestDTO userRequestDTO, String password) {
        this.name = userRequestDTO.name();
        this.email = userRequestDTO.email();
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
    }


    @Override
    public String getUsername() {
        return email;
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
        
        return true;
    }
}
