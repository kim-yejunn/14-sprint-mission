package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserName(@NotBlank String username);

    Optional<User> findByEmail(@NotBlank String email);

    @Override
    @EntityGraph(attributePaths = {"status", "profile"})
    List<User> findAll();
}
