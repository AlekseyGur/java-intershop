package ru.alexgur.intershop.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ru.alexgur.intershop.TestWebConfiguration;
import ru.alexgur.intershop.item.model.ActionType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class CartControllerTest extends TestWebConfiguration {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getCartItems() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"));
    }

    @Test
    void updateCartItemQuantity() throws Exception {
        mockMvc.perform(get("/cart"));

        mockMvc.perform(post("/cart/items/1")
                .param("action", ActionType.PLUS.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("/cart")));
    }

    @Test
    void buy() throws Exception {
        mockMvc.perform(post("/cart/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("/orders/")))
                .andExpect(header().string("Location", endsWith("?newOrder=true")));
    }
}