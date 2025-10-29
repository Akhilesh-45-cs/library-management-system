package it.objectmethod.demo.spring.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BookObjectTest {

    @Test
    void gettersAndSetters_work() {
        BookObject b = new BookObject();
        b.setId(5L);
        b.setTitle("T");
        b.setAuthor("A");
        b.setIsbn("I");

        assertEquals(5L, b.getId());
        assertEquals("T", b.getTitle());
        assertEquals("A", b.getAuthor());
        assertEquals("I", b.getIsbn());
    }

    @Test
    void memberAssociation_setAndGet() {
        BookObject b = new BookObject();
        it.objectmethod.demo.spring.models.MemberObject m = new it.objectmethod.demo.spring.models.MemberObject();
        m.setId(7L);
        b.setMemberObject(m);
        assertNotNull(b.getMemberObject());
        assertEquals(7L, b.getMemberObject().getId());
    }
}
