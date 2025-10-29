package it.objectmethod.demo.spring.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MemberObjectTest {

    @Test
    void gettersAndSetters_work() {
        MemberObject m = new MemberObject();
        m.setId(2L);
        m.setName("John");
        m.setLastName("Smith");
        m.setComplaints(0);

        assertEquals(2L, m.getId());
        assertEquals("John", m.getName());
        assertEquals("Smith", m.getLastName());
        assertEquals(0, m.getComplaints());
    }

    @Test
    void defaultComplaints_isNullUntilSet() {
        MemberObject m = new MemberObject();
        assertNull(m.getComplaints());
    }
}
