package ru.alexgur.intershop.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CustomUserDetails implements UserDetails {
    private UUID userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
}