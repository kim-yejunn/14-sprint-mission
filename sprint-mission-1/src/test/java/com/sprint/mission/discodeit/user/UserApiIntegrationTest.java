package com.sprint.mission.discodeit.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_success() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest", "", "application/json",
            "{\"username\":\"kyj\",\"password\":\"pw1234\",\"email\":\"a@b.com\"}".getBytes());

        mockMvc.perform(multipart("/api/users").file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("kyj"));

        assertThat(userRepository.findByUserName("kyj")).isPresent();
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_success() throws Exception {
        User saved = userRepository.save(
            User.create("kkk", "pw1234", "b@c.com", null));

        mockMvc.perform(delete("/api/users/{userId}", saved.getId()))
            .andExpect(status().isNoContent());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}
