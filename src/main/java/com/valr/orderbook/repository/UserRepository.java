package com.valr.orderbook.repository;

import com.valr.orderbook.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing user data.
 */
@Component
@Data
public class UserRepository {
    List<User> users;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Constructor for UserRepository.
     * Initializes the user list.
     */
    public UserRepository() {
        users = new LinkedList<>();
    }

    /**
     * Adds initial data to the repository on startup.
     * This is just for easier presentation purposes; for a live system, initialization would be added in unit tests.
     */
    @PostConstruct
    public void insertData() {
        createSystemUsers(users);
    }

    /**
     * Creates system users and adds them to the user list.
     *
     * @param users the list of users to add the system users to
     */
    private void createSystemUsers(List<User> users) {
        users.add(new User("Administrator", "Administrator", "admin@valr.com",
                "admin", passwordEncoder.encode("admin")));
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return an Optional containing the authenticated user if found, otherwise empty
     */
    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && passwordEncoder.matches(password, u.getPassword()))
                .findFirst();
    }
}