package com.valr.orderbook.repository;

import com.valr.orderbook.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
@Data
public class UserRepository {
    List<User> users;

    public UserRepository() {
        users = new LinkedList<>();
    }


    @PostConstruct
    public void insertData() {
        createSystemUsers(users);
    }

    private void createSystemUsers(List<User> users) {
        users.add(User.builder()
                .firstName("Administrator")
                .lastName("Administrator")
                .email("admin@valr.com")
                .username("admin")
                .password("admin")
                .build());

    }

    public Optional<User> login(String username, String password) {
        Optional<User> user = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
        System.out.println("User " + user.map(value -> value.getUsername() + " logged in successfully").orElse(" failed to login"));
        return user;
    }
}
