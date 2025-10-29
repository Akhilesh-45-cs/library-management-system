package it.objectmethod.demo.spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import it.objectmethod.demo.spring.models.BookObject;
import it.objectmethod.demo.spring.models.MemberObject;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testFindByMemberObjectIdNullAndNotNull() {
        // Create members
        MemberObject member = new MemberObject();
        member.setName("John Doe");
        member = memberRepository.save(member);

        // Create books
        BookObject availableBook = new BookObject();
        availableBook.setTitle("Available Book");
        availableBook = bookRepository.save(availableBook);

        BookObject borrowedBook = new BookObject();
        borrowedBook.setTitle("Borrowed Book");
        borrowedBook.setMemberObject(member);
        borrowedBook = bookRepository.save(borrowedBook);

        // Fetch books with null member
        List<BookObject> availableBooks = bookRepository.findByMemberObjectIdNull();
        assertThat(availableBooks).extracting(BookObject::getTitle).contains("Available Book");

        // Fetch borrowed books
        List<BookObject> borrowedBooks = bookRepository.findByMemberObjectIdNotNull();
        assertThat(borrowedBooks).extracting(BookObject::getTitle).contains("Borrowed Book");
    }

    @Test
    void testFindByMemberObjectId() {
        MemberObject member = new MemberObject();
        member.setName("Alice");
        member = memberRepository.save(member);

        BookObject book = new BookObject();
        book.setTitle("Alice’s Book");
        book.setMemberObject(member);
        bookRepository.save(book);

        List<BookObject> books = bookRepository.findByMemberObjectId(member.getId());
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Alice’s Book");
    }

    @Test
    void findByMemberObjectId_returnsBooksForMember() {
        MemberObject m = new MemberObject();
        m.setName("Sam");
        m.setLastName("S");
        memberRepository.save(m);

        BookObject b = new BookObject();
        b.setTitle("ForSam");
        b.setAuthor("A");
        b.setMemberObject(m);
        bookRepository.save(b);

        java.util.List<BookObject> found = bookRepository.findByMemberObjectId(m.getId());
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getMemberObject().getId()).isEqualTo(m.getId());
    }
}
