package ru.alexgur.intershop.user.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.user.model.User;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1;")
    Mono<User> findByUsername(@Param("username") String username);

}
