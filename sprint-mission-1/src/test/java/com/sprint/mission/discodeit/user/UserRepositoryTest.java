package com.sprint.mission.discodeit.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("userName으로 사용자 조회 성공")
    void findByUserName_success() {
        userRepository.save(User.create("kyj", "pw1234", "a@b.com", null));
        testEntityManager.flush();
        testEntityManager.clear();

        Optional<User> found = userRepository.findByUserName("kyj");

        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("kyj");
    }

    @Test
    @DisplayName("userName으로 사용자 조회 실패 - 없는 이름")
    void findByUserName_fail_notFound() {
        Optional<User> found = userRepository.findByUserName("nobody");
        assertThat(found).isEmpty();
    }
}
