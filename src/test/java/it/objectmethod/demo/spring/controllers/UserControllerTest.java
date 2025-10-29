package it.objectmethod.demo.spring.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import it.objectmethod.demo.spring.services.JwtService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.objectmethod.demo.spring.models.UserObject;
import it.objectmethod.demo.spring.services.UsersService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private JwtService jwtService;

    private UserObject u1;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        u1 = new UserObject();
        u1.setId(1L);
        u1.setUsername("test");
        u1.setPassword("pwd");
    }

    @Test
    void register_returnsOk_whenNewUser() throws Exception {
        when(usersService.register("test", "pwd")).thenReturn(u1);

        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u1))).andExpect(status().isOk());
    }

    @Test
    void login_returnsBadRequest_whenInvalidCredentials() throws Exception {
        when(usersService.login("test", "pwd")).thenReturn(null);

        UserObject login = new UserObject();
        login.setUsername("test");
        login.setPassword("pwd");

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))).andExpect(status().isBadRequest());
    }
}
