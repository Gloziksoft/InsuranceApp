package com.insurance.app.data.repositories;

import com.insurance.app.data.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

/**
 * Repository for managing UserEntity persistence.
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * Finds a user by their email address.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     */
    boolean existsByEmail(String email);
}
