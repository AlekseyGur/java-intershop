package ru.alexgur.intershop.user.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.user.model.CustomUserDetails;
import ru.alexgur.intershop.user.model.User;
import ru.alexgur.intershop.user.repository.UserRepository;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::fromUserToCustomUserDetails);
    }

    private CustomUserDetails fromUserToCustomUserDetails(User user) {
        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Arrays.stream(user.getRoles().split(","))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList())
                .enabled(user.isActive())
                .accountNonExpired(user.isActive())
                .credentialsNonExpired(user.isActive())
                .accountNonLocked(user.isActive())
                .build();
    }
}
