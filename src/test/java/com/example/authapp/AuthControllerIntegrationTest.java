package com.example.authapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAndLoginShouldReturnJwt() throws Exception {
        String registerRequest = """
            {
              "name": "Alice",
              "email": "alice@example.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(registerRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("alice@example.com"));

        String loginRequest = """
            {
              "email": "alice@example.com",
              "password": "password123"
            }
            """;

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
            .andExpect(status().isOk())
            .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/test/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
}
