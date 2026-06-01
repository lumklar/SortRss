package io.github.lumklar.sortrss.server.infrastructure.persistence.repository;

import io.github.lumklar.sortrss.server.infrastruncture.persistence.entity.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);
    Boolean existsByUsername(String username);
}
