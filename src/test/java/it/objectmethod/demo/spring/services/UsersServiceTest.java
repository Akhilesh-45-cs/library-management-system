package it.objectmethod.demo.spring.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.objectmethod.demo.spring.models.UserObject;
import it.objectmethod.demo.spring.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsersService usersService;

    private UserObject testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserObject();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole("user");
    }

    @Test
    void testRegisterNewUser_Success() {
        // Arrange
        when(userRepository.register("testuser")).thenReturn(null);
        when(userRepository.save(any(UserObject.class))).thenReturn(testUser);

        // Act
        UserObject result = usersService.register("testuser", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("user", result.getRole());
        verify(userRepository).save(any(UserObject.class));
    }

    @Test
    void testRegisterNewUser_UserExists() {
        // Arrange
        when(userRepository.register("testuser")).thenReturn(testUser);

        // Act
        UserObject result = usersService.register("testuser", "password123");

        // Assert
        assertNull(result);
        verify(userRepository, never()).save(any(UserObject.class));
    }

    @Test
    void testLogin_Success() {
        // Arrange
        String hashedPwd = org.springframework.util.DigestUtils.md5DigestAsHex("password123".getBytes());
        when(userRepository.login("testuser", hashedPwd)).thenReturn(testUser);
        when(jwtService.createJWTToken(testUser)).thenReturn("test.jwt.token");

        // Act
        String token = usersService.login("testuser", "password123");

        // Assert
        assertNotNull(token);
        assertEquals("test.jwt.token", token);
        verify(jwtService).createJWTToken(testUser);
    }

    @Test
    void testLogin_Failure() {
        // Arrange
        String hashedWrong = org.springframework.util.DigestUtils.md5DigestAsHex("wrongpassword".getBytes());
        when(userRepository.login("testuser", hashedWrong)).thenReturn(null);

        // Act
        String token = usersService.login("testuser", "wrongpassword");

        // Assert
        assertNull(token);
        verify(jwtService, never()).createJWTToken(any());
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(testUser));

        // Act
        Iterable<UserObject> users = usersService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.iterator().hasNext());
        verify(userRepository).findAll();
    }

    @Test
    void testEditUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
        when(userRepository.save(any(UserObject.class))).thenReturn(testUser);

        // Act
        usersService.editUser(1L, "newusername", "newpassword");

        // Assert
        verify(userRepository).save(any(UserObject.class));
    }

    @Test
    void testRegister_hashesPasswordBeforeSave() {
        // Arrange
        when(userRepository.register("alice")).thenReturn(null);
        // capture saved user
        java.util.concurrent.atomic.AtomicReference<UserObject> captured = new java.util.concurrent.atomic.AtomicReference<>();
        when(userRepository.save(any(UserObject.class))).thenAnswer(invocation -> {
            UserObject u = invocation.getArgument(0);
            captured.set(u);
            u.setId(99L);
            return u;
        });

        // Act
        UserObject created = usersService.register("alice", "s3cr3t");

        // Assert
        assertNotNull(created);
        assertEquals(99L, created.getId());
        String expectedHash = org.springframework.util.DigestUtils.md5DigestAsHex("s3cr3t".getBytes());
        assertEquals(expectedHash, captured.get().getPassword());
    }
}