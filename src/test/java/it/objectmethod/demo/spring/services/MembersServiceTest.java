package it.objectmethod.demo.spring.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.objectmethod.demo.spring.models.BookObject;
import it.objectmethod.demo.spring.models.MemberObject;
import it.objectmethod.demo.spring.repository.BookRepository;
import it.objectmethod.demo.spring.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MembersServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private MembersService membersService;

    private MemberObject m1;
    private BookObject b1;

    @BeforeEach
    void setUp() {
        m1 = new MemberObject();
        m1.setId(10L);
        m1.setName("Alice");
        m1.setLastName("Liddell");

        b1 = new BookObject();
        b1.setId(100L);
        b1.setTitle("Wonder");
        b1.setAuthor("Carroll");
        b1.setMemberObject(m1);
    }

    @Test
    void testGetAllMembers() {
        when(memberRepository.findAll()).thenReturn(Arrays.asList(m1));

        List<MemberObject> result = membersService.getAllMembers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
        verify(memberRepository).findAll();
    }

    @Test
    void testGetOneMember_Found() {
        when(memberRepository.findById(10L)).thenReturn(Optional.of(m1));

        Object result = membersService.getOneMember(10L);

        assertNotNull(result);
        assertTrue(result instanceof MemberObject);
        assertEquals("Alice", ((MemberObject) result).getName());
        verify(memberRepository).findById(10L);
    }

    @Test
    void testGetOneMember_NotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        Object result = membersService.getOneMember(999L);

        assertNull(result);
        verify(memberRepository).findById(999L);
    }

    @Test
    void testRemoveMember_disassociatesBooksAndDeletesMember() {
        when(bookRepository.findByMemberObjectId(10L)).thenReturn(Arrays.asList(b1));

        membersService.removeMember(10L);

        ArgumentCaptor<BookObject> captor = ArgumentCaptor.forClass(BookObject.class);
        verify(bookRepository).save(captor.capture());
        BookObject saved = captor.getValue();
        assertNull(saved.getMemberObject(), "Expected member association to be cleared before saving the book");

        verify(memberRepository).deleteById(10L);
    }

    @Test
    void testAddNewMember_callsSave() {
        membersService.addNewMember(m1);
        verify(memberRepository).save(m1);
    }

    @Test
    void testEditMember_callsSave() {
        membersService.editMember(m1);
        verify(memberRepository).save(m1);
    }

    @Test
    void testClearList_callsDeleteAll() {
        membersService.clearList();
        verify(memberRepository).deleteAll();
    }

    @Test
    void testRemoveMember_noBooks_deletesMember() {
        when(bookRepository.findByMemberObjectId(10L)).thenReturn(java.util.Collections.emptyList());

        membersService.removeMember(10L);

        verify(bookRepository, never()).save(any(BookObject.class));
        verify(memberRepository).deleteById(10L);
    }
}
