package it.objectmethod.demo.spring.controllers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import it.objectmethod.demo.spring.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import it.objectmethod.demo.spring.services.JwtService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import it.objectmethod.demo.spring.models.MemberObject;
import it.objectmethod.demo.spring.services.MembersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(MembersController.class)
public class MembersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembersService membersService;

    @MockBean
    private JwtService jwtService;

    private ObjectMapper mapper = new ObjectMapper();

    private MemberObject m1;

    @BeforeEach
    void setUp() {
        m1 = new MemberObject();
        m1.setId(1L);
        m1.setName("John");
        m1.setLastName("Doe");
    }

    @Test
    void getAllMembers_returnsList() throws Exception {
    when(membersService.getAllMembers()).thenReturn(Arrays.asList(m1));
    when(jwtService.checkJWTToken(anyString())).thenReturn(true);

    mockMvc.perform(get("/members/all").header(Constants.TOKEN_HEADER_NAME, "valid-token").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    void getAllMembers_withoutToken_returnsForbidden() throws Exception {
        when(membersService.getAllMembers()).thenReturn(Arrays.asList(m1));

        mockMvc.perform(get("/members/all").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    void addNewMember_postsJson_andCallsService() throws Exception {
        MemberObject m = new MemberObject();
        m.setName("Z");

        when(jwtService.checkJWTToken(anyString())).thenReturn(true);

        mockMvc.perform(post("/members/add").header(Constants.TOKEN_HEADER_NAME, "t").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(m))).andExpect(status().isOk());

        verify(membersService).addNewMember(any(MemberObject.class));
    }

    @Test
    void editMember_putJson_andCallsService() throws Exception {
        MemberObject m = new MemberObject();
        m.setId(5L);
        m.setName("E");

        when(jwtService.checkJWTToken(anyString())).thenReturn(true);

        mockMvc.perform(put("/members/edit").header(Constants.TOKEN_HEADER_NAME, "t").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(m))).andExpect(status().isOk());

        verify(membersService).editMember(any(MemberObject.class));
    }

    @Test
    void removeMember_delete_callsService() throws Exception {
        when(jwtService.checkJWTToken(anyString())).thenReturn(true);

        mockMvc.perform(delete("/members/remove/10").header(Constants.TOKEN_HEADER_NAME, "t")).andExpect(status().isOk());

        verify(membersService).removeMember(10L);
    }
}
