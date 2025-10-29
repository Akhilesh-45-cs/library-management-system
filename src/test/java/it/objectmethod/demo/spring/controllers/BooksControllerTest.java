package it.objectmethod.demo.spring.controllers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import it.objectmethod.demo.spring.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import it.objectmethod.demo.spring.services.JwtService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import it.objectmethod.demo.spring.models.BookObject;
import it.objectmethod.demo.spring.services.BooksService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BooksController.class)
public class BooksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BooksService booksService;

    @MockBean
    private JwtService jwtService;

    private ObjectMapper mapper = new ObjectMapper();

    private BookObject b1;

    @BeforeEach
    void setUp() {
        b1 = new BookObject();
        b1.setId(1L);
        b1.setTitle("A");
        b1.setAuthor("Author");
        b1.setIsbn("123");
    }

    @Test
    void getAllBooks_returnsList() throws Exception {
    when(booksService.getAllBooks()).thenReturn(Arrays.asList(b1));
    when(jwtService.checkJWTToken(anyString())).thenReturn(true);

    mockMvc.perform(get("/books/all").header(Constants.TOKEN_HEADER_NAME, "valid-token").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("A"));
    }

    @Test
    void getAllBooks_withoutToken_returnsForbidden() throws Exception {
        when(booksService.getAllBooks()).thenReturn(Arrays.asList(b1));

        mockMvc.perform(get("/books/all").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    void addNewBook_postsJson_andCallsService() throws Exception {
        BookObject newBook = new BookObject();
        newBook.setTitle("New");
        newBook.setAuthor("Auth");

        when(jwtService.checkJWTToken(anyString())).thenReturn(true);

        mockMvc.perform(post("/books/add").header(Constants.TOKEN_HEADER_NAME, "tok").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newBook))).andExpect(status().isOk());

        verify(booksService).addNewBook(any(BookObject.class));
    }

    @Test
    void editBook_putJson_andCallsService() throws Exception {
        BookObject up = new BookObject();
        up.setId(5L);
        up.setTitle("U");

        when(jwtService.checkJWTToken(anyString())).thenReturn(true);

        mockMvc.perform(put("/books/edit").header(Constants.TOKEN_HEADER_NAME, "t").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(up))).andExpect(status().isOk());

        verify(booksService).editBook(any(BookObject.class));
    }

    @Test
    void removeBook_delete_callsService() throws Exception {
        when(jwtService.checkJWTToken(anyString())).thenReturn(true);

        mockMvc.perform(delete("/books/remove/7").header(Constants.TOKEN_HEADER_NAME, "t")).andExpect(status().isOk());

        verify(booksService).removeBook(7L);
    }
}
