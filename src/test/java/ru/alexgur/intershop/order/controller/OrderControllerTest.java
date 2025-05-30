package ru.alexgur.intershop.order.controller;

import ru.alexgur.intershop.TestWebConfiguration;
import ru.alexgur.intershop.item.model.ActionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class OrderControllerTest extends TestWebConfiguration {

        @Autowired
        private WebApplicationContext webApplicationContext;
        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Test
        void getOrders() throws Exception {
                mockMvc.perform(get("/orders"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("orders"));
        }

        @Test
        void getOrderById() throws Exception {
                mockMvc.perform(get("/cart"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/orders/1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("order"));
        }

        @Test
        void getOrdersMany() throws Exception {

                mockMvc.perform(get("/cart"));

                mockMvc.perform(post("/cart/items/1")
                                .param("action", ActionType.PLUS.toString()));

                mockMvc.perform(get("/orders"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("orders"));
        }
}