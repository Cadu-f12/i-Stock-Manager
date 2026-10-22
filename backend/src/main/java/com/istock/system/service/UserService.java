package com.istock.system.service;

import com.istock.system.entities.User;
import com.istock.system.exception.UserAlreadyExist;
import com.istock.system.exception.UserNotFound;
import com.istock.system.exception.UserNotFoundWithEmail;
import com.istock.system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));
        return user;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User createUser(User user) {

        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExist(user);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }

    public User fetchUser(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundWithEmail(email));
        return user;
    }

    public User updateUser(Long id, User user) {

        User userResponse = repository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword() );

        return repository.save(userResponse);
    }

        public User deleteUser(Long id) {

            User userResponse = repository.findById(id)
                    .orElseThrow(() -> new UserNotFound(id));

            repository.delete(userResponse);

            return userResponse;
        }
}
