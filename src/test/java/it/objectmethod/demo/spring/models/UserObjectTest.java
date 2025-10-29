package it.objectmethod.demo.spring.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserObjectTest {

    @Test
    void gettersAndSetters_work() {
        UserObject u = new UserObject();
        u.setId(3L);
        u.setUsername("u");
        u.setPassword("p");

        assertEquals(3L, u.getId());
        assertEquals("u", u.getUsername());
        assertEquals("p", u.getPassword());
    }

    @Test
    void roleGetterSetter_work() {
        UserObject u = new UserObject();
        u.setRole("admin");
        assertEquals("admin", u.getRole());
    }
}
