package com.istock.system.controller;

import com.istock.system.entities.User;
import com.istock.system.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
     }

     @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
         return service.getUser(id);
     }

     @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

     @PostMapping("/createUser")
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }

     @PostMapping("/fetchUser")
    public User fetchUser(@RequestBody User user) {
        return service.fetchUser(user.getEmail());
    }

    @PutMapping("/updateUser/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {

        return service.updateUser(id, user);
    }

    @DeleteMapping("/deleteUser/{id}")
    public User deleteUser(@PathVariable Long id) {

        return service.deleteUser(id);
    }
}
