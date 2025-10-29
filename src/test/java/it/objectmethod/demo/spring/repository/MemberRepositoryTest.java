package it.objectmethod.demo.spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import it.objectmethod.demo.spring.models.MemberObject;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndFindMember() {
        MemberObject member = new MemberObject();
        member.setName("John Doe");

        memberRepository.save(member);
        assertThat(member.getId()).isNotNull();

        MemberObject found = memberRepository.findById(member.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John Doe");
    }
    
    @Test
    void deleteAll_removesAllMembers() {
        MemberObject m1 = new MemberObject();
        m1.setName("A");
        memberRepository.save(m1);
        
        memberRepository.deleteAll();
        Iterable<MemberObject> all = memberRepository.findAll();
        assertThat(all).isEmpty();
    }
}
