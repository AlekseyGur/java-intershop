package ru.alexgur.intershop.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;
import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.service.ItemServiceImpl;
import ru.alexgur.intershop.system.exception.PaymentException;
import ru.alexgur.intershop.user.model.CustomUserDetails;
import ru.alexgur.intershop.user.model.User;
import ru.alexgur.intershop.user.repository.UserRepository;
import ru.alexgur.payment.model.Balance;

@SpringBootTest
@AutoConfigureWebTestClient
class CartControllerTest extends BaseTest {

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    @Autowired
    private UserRepository userRepository;

    UUID firstSavedItemId;
    ItemDto firstSavedItem;
    User userSimple;
    User userAdmin;
    CustomUserDetails userSimpleCustom;
    CustomUserDetails userAdminCustom;

    @BeforeEach
    public void getFirstSavedItemId() {
        firstSavedItem = itemServiceImpl.getAll(0, 1, "", SortType.ALPHA).block().getContent().get(0);
        firstSavedItemId = firstSavedItem.getId();

        userAdmin = new User(
                UUID.fromString("1e0408ff-d416-4a42-829f-54bcec748c11"),
                "admin",
                "$2a$10$MfwZEA6sSlMYZ9pnu3moG.M1SQ7wrAWi3MKTNOKKLBKLvLJZ1a9Ya",
                true,
                "ADMIN");

        userSimple = new User(
                UUID.fromString("b41f2a31-f2aa-42cd-b86a-e7db862e1bb5"),
                "user",
                "$2a$10$MfwZEA6sSlMYZ9pnu3moG.M1SQ7wrAWi3MKTNOKKLBKLvLJZ1a9Ya",
                true,
                "USER");

        userAdminCustom = CustomUserDetails.customUserDetailsBuilder()
                .userId(userAdmin.getId())
                .username(userAdmin.getUsername())
                .password(userAdmin.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .build();

        userSimpleCustom = CustomUserDetails.customUserDetailsBuilder()
                .userId(userSimple.getId())
                .username(userSimple.getUsername())
                .password(userSimple.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .build();

        userRepository.save(userAdmin).subscribe();
        userRepository.save(userSimple).subscribe();
    }

    protected UUID testUserId;
    protected CustomUserDetails mockUser;

    @BeforeEach
    void setUp() {
        User userEntity = User.builder()
                .username("test")
                .password("test")
                .active(true)
                .roles("USER")
                .build();
        this.testUserId = Objects.requireNonNull(userRepository.save(userEntity).block()).getId();
        this.mockUser = CustomUserDetails.customUserDetailsBuilder()
                .userId(this.testUserId)
                .username("test")
                .password("test")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @AfterEach
    void cleanDb() {
        userRepository.deleteById(testUserId).then().block();
    }

    @Test
    public void getCartItems() throws Exception {
        SecurityContextHolder.clearContext();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                mockUser.getAuthorities());

        // SecurityContextHolder.getContext().setAuthentication(authentication);

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockAuthentication(authentication))
                .get().uri("/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void updateCartItemQuantity() throws Exception {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .post().uri("/cart/items/" + firstSavedItemId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void buy() throws Exception {
        when(paymentService.doPayment(anyDouble())).thenReturn(Mono.empty());
        Balance balanceEntity = new Balance(firstSavedItem.getPrice() + 100.0);
        when(paymentService.getBalance()).thenReturn(Mono.just(balanceEntity));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .post().uri("/cart/items/" + firstSavedItemId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/orders");

    }

    @Test
    public void buyNotEnoughMoneyError() throws Exception {
        when(paymentService.doPayment(anyDouble()))
                .thenReturn(Mono.error(new PaymentException("Ошибка выполнения платежа")));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .post().uri("/cart/items/" + firstSavedItemId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimpleCustom))
                .post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/error\\?reason=low_balance");

    }
}