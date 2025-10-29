package it.objectmethod.demo.spring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import it.objectmethod.demo.spring.models.BookObject;
import it.objectmethod.demo.spring.models.MemberObject;
import it.objectmethod.demo.spring.repository.BookRepository;
import it.objectmethod.demo.spring.repository.MemberRepository;

@Service
public class MembersService {

	@Autowired
	private MemberRepository mr;

	public @ResponseBody List<MemberObject> getAllMembers() {
		return mr.findAll();
	}

	public Object getOneMember(Long index) {
		return mr.findById(index).orElse(null);
	}

	@Autowired
	private BookRepository bookRepository;

	@Transactional
	public void removeMember(Long index) {
		// First, get all books associated with this member and set their memberObject to null
		List<BookObject> memberBooks = bookRepository.findByMemberObjectId(index);
		for (BookObject book : memberBooks) {
			book.setMemberObject(null);
			bookRepository.save(book);
		}
		// Then delete the member
		mr.deleteById(index);
	}

	public void addNewMember(MemberObject member) {
		mr.save(member);
	}

	public void editMember(MemberObject member) {
		mr.save(member);
	}

	public void clearList() {
		mr.deleteAll();
	}

}
