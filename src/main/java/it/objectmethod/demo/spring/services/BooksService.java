package it.objectmethod.demo.spring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.objectmethod.demo.spring.models.BookObject;
import it.objectmethod.demo.spring.models.MemberObject;
import it.objectmethod.demo.spring.repository.BookRepository;
import it.objectmethod.demo.spring.repository.MemberRepository;

@Service
public class BooksService {
	@Autowired
	private BookRepository br;
	
	@Autowired
	private MemberRepository memberRepository;

	public List<BookObject> getAllBooks() {
		return br.findAll();
	}

	public Object getOneBook(Long index) {
		return br.findById(index).orElse(null);
	}

	public void removeBook(Long index) {
		br.deleteById(index);
	}

	public void clearList() {
		br.deleteAll();

	}

	public void addNewBook(BookObject book) {
		br.save(book);
	}

	@Transactional
	public void editBook(BookObject book) {
		BookObject existingBook = br.findById(book.getId()).orElse(null);
		if (existingBook != null) {
			if (book.getMemberObject() != null) {
				// Verify that the member exists before associating
				MemberObject member = memberRepository.findById(book.getMemberObject().getId()).orElse(null);
				if (member == null) {
					book.setMemberObject(null); // Reset if member doesn't exist
				}
			}
			br.save(book);
		}
	}

	public List<BookObject> getAllNull() {
		return br.findByMemberObjectIdNull();
	}

	public List<BookObject> getAllNotNull() {
		return br.findByMemberObjectIdNotNull();
	}

}
