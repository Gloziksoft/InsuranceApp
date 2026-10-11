package com.insurance.app.models.services;

import com.insurance.app.data.entities.UserEntity;
import com.insurance.app.data.repositories.UserRepository;
import com.insurance.app.models.dto.RegistrationDTO;
import com.insurance.app.models.exceptions.DuplicateEmailException;
import com.insurance.app.models.exceptions.PasswordsDoNotEqualException;
import com.insurance.app.models.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing users and authentication.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository usersRepository;

    @Value("${app.base-url}")
    private String baseUrl;


    /**
     * Creates a new user with optional admin privileges.
     *
     * @param registrationDTO User registration data.
     * @param isAdmin         Whether the new user should have admin role.
     */
    @Override
    public void create(RegistrationDTO registrationDTO, boolean isAdmin) {
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new PasswordsDoNotEqualException();
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setFirstName(registrationDTO.getFirstName());
        userEntity.setLastName(registrationDTO.getLastName());
        userEntity.setEmail(registrationDTO.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        userEntity.setAdmin(isAdmin);

        try {
            usersRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    /**
     * Loads a user by their email address (username) for authentication.
     *
     * @param username The email address used as username.
     * @return UserDetails of the authenticated user.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Username, " + username + " not found"));
    }

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param token           Password reset token.
     * @param password        New password.
     * @param confirmPassword Password confirmation.
     */
    @Override
    public boolean resetPassword(String token, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordsDoNotEqualException();
        }

        Optional<UserEntity> optionalUser = usersRepository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            return false;
        }

        UserEntity user = optionalUser.get();

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(password));

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        usersRepository.save(user);

        return true;
    }

    /**
     * Creates a password reset token and sends a reset link to the user's email.
     *
     * @param email User's email address.
     */
    @Override
    public void forgotPassword(String email) {
        Optional<UserEntity> optionalUser = usersRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return;
        }

        UserEntity user = optionalUser.get();

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        usersRepository.save(user);

        String resetLink =
                baseUrl + "/account/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    @Override
    public boolean isResetTokenValid(String token) {
        Optional<UserEntity> optionalUser = usersRepository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            return false;
        }

        UserEntity user = optionalUser.get();

        return user.getResetTokenExpiry() != null
                && user.getResetTokenExpiry().isAfter(LocalDateTime.now());
    }
}