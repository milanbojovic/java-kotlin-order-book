package com.valr.orderbook.repository;

import com.valr.orderbook.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
@Data
public class UserRepository {
    List<User> users;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public UserRepository() {
        users = new LinkedList<>();
    }

    @PostConstruct
    public void insertData() {
        createSystemUsers(users);
    }

    private void createSystemUsers(List<User> users) {
        users.add(new User("Administrator", "Administrator", "admin@valr.com",
                adminUsername, passwordEncoder.encode(adminPassword)));
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && passwordEncoder.matches(password, u.getPassword()))
                .findFirst();
    }
}