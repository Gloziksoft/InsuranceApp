package com.insurance.app.models.services;

import com.insurance.app.models.dto.RegistrationDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for user management and authentication.
 */
public interface UserService extends UserDetailsService {

    /**
     * Creates a new user.
     *
     * @param registrationDTO User registration data.
     * @param isAdmin Whether the user should have admin privileges.
     */
    void create(RegistrationDTO registrationDTO, boolean isAdmin);

    /**
     * Sends a password reset link to the user's email address.
     *
     * @param email User's email address.
     */
    void forgotPassword(String email);

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param token Password reset token.
     * @param password New password.
     * @param confirmPassword Password confirmation.
     * @return true if the password was reset successfully, otherwise false.
     */
    boolean resetPassword(String token, String password, String confirmPassword);

    boolean isResetTokenValid(String token);
}