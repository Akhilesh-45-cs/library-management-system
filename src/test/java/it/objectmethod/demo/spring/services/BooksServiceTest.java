package it.objectmethod.demo.spring.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import it.objectmethod.demo.spring.repository.MemberRepository;
import it.objectmethod.demo.spring.models.BookObject;
import it.objectmethod.demo.spring.models.MemberObject;
import it.objectmethod.demo.spring.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BooksServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BooksService booksService;

    private BookObject testBook;

    @BeforeEach
    void setUp() {
        testBook = new BookObject();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        // testBook availability is determined by memberObject presence in the current codebase
    }

    @Test
    void testGetAllBooks() {
        // Arrange
        List<BookObject> expectedBooks = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // Act
        List<BookObject> actualBooks = booksService.getAllBooks();

        // Assert
        assertNotNull(actualBooks);
        assertEquals(1, actualBooks.size());
        assertEquals("Test Book", actualBooks.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void testGetOneBook_Found() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act
        Object result = booksService.getOneBook(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof BookObject);
        assertEquals("Test Book", ((BookObject) result).getTitle());
    }

    @Test
    void testGetOneBook_NotFound() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Object result = booksService.getOneBook(999L);

        // Assert
        // current service returns null when book is not found
        assertNull(result);
    }

    @Test
    void testAddNewBook() {
        // Arrange
        when(bookRepository.save(any(BookObject.class))).thenReturn(testBook);

        // Act
        booksService.addNewBook(testBook);

        // Assert
        verify(bookRepository).save(testBook);
    }

    @Test
    void testEditBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(BookObject.class))).thenReturn(testBook);

        BookObject updatedBook = new BookObject();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Updated Author");

        // Act
        booksService.editBook(updatedBook);

        // Assert
        verify(bookRepository).save(any(BookObject.class));
    }

    @Test
    void testRemoveBook() {
        // Arrange
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        booksService.removeBook(1L);

        // Assert
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void testGetAllNull() {
        // Arrange
        // In this project availability is represented by memberObject being null (available) or not null (checked out)
        BookObject checkedOutBook = new BookObject();
        checkedOutBook.setMemberObject(new it.objectmethod.demo.spring.models.MemberObject());
        List<BookObject> expectedBooks = Arrays.asList(checkedOutBook);
        when(bookRepository.findByMemberObjectIdNotNull()).thenReturn(expectedBooks);

        // Act
        List<BookObject> actualBooks = booksService.getAllNotNull();

        // Assert
        assertNotNull(actualBooks);
        assertEquals(1, actualBooks.size());
        assertNotNull(actualBooks.get(0).getMemberObject());
    }
    
    @Test
    void testGetAllNull_WithAvailableBooks() {
        // Arrange
        BookObject availableBook = new BookObject();
        availableBook.setMemberObject(null);
        List<BookObject> expectedBooks = Arrays.asList(availableBook);
        when(bookRepository.findByMemberObjectIdNull()).thenReturn(expectedBooks);

        // Act
        List<BookObject> actualBooks = booksService.getAllNull();

        // Assert
        assertNotNull(actualBooks);
        assertEquals(1, actualBooks.size());
        assertNull(actualBooks.get(0).getMemberObject());
    }

    @Test
    void testEditBook_nonExistingMember_resetsMember() {
        // Arrange
        BookObject b = new BookObject();
        b.setId(10L);
        b.setTitle("X");
        MemberObject fakeMember = new MemberObject();
        fakeMember.setId(999L);
        b.setMemberObject(fakeMember);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(b));
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        booksService.editBook(b);

        // Assert - save called with memberObject reset to null
        verify(bookRepository).save(argThat(saved -> saved.getMemberObject() == null));
    }
}