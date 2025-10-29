package it.objectmethod.demo.spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import it.objectmethod.demo.spring.models.UserObject;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testLoginAndRegisterQueries() {
        // Insert user manually using entity manager (because MD5 is handled by DB)
        UserObject user = new UserObject();
        user.setUsername("admin");
        user.setPassword(org.apache.commons.codec.digest.DigestUtils.md5Hex("1234"));
        entityManager.persist(user);
        entityManager.flush();

        // Test register query (find by username)
        UserObject existingUser = userRepository.register("admin");
        assertThat(existingUser).isNotNull();
        assertThat(existingUser.getUsername()).isEqualTo("admin");

        // Test login query - supply the hashed password (service would hash before calling repository)
        String hashed = org.apache.commons.codec.digest.DigestUtils.md5Hex("1234");
        UserObject loggedUser = userRepository.login("admin", hashed);
        assertThat(loggedUser).isNotNull();
    }
}
