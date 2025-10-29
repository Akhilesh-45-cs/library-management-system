package it.objectmethod.demo.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.objectmethod.demo.spring.models.BookObject;

public interface BookRepository extends JpaRepository<BookObject, Long> {
	
	List<BookObject> findByMemberObjectIdNull();

	List<BookObject> findByMemberObjectIdNotNull();
	
	List<BookObject> findByMemberObjectId(Long memberId);

}
