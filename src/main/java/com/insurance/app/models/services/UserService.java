package com.insurance.app.models.services;

import com.insurance.app.models.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for user management and authentication.
 */
public interface UserService extends UserDetailsService {

    /**
     * Creates a new user.
     * @param user User data.
     * @param isAdmin Whether the user should have admin privileges.
     */
    void create(UserDTO user, boolean isAdmin);
}
