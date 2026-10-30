package com.istock.system.admin;

import com.istock.system.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FrontendUserController {
    private final AdminUserService users;
    private final UserService accounts;

    public FrontendUserController(AdminUserService users, UserService accounts) {
        this.users = users;
        this.accounts = accounts;
    }

    @GetMapping("/auth/me")
    public AdminUserService.UserView me(Authentication auth) {
        return AdminUserService.UserView.of(accounts.fetchUser(auth.getName()));
    }

    @GetMapping("/admin/users")
    public List<AdminUserService.UserView> list() { return users.list(); }

    @PostMapping("/admin/users")
    public AdminUserService.UserView create(@RequestBody AdminUserService.UserInput input, Authentication auth) {
        return users.save(null, input, accounts.fetchUser(auth.getName()).getId());
    }

    @PutMapping("/admin/users/{id}")
    public AdminUserService.UserView update(@PathVariable long id, @RequestBody AdminUserService.UserInput input, Authentication auth) {
        return users.save(id, input, accounts.fetchUser(auth.getName()).getId());
    }

    @DeleteMapping("/admin/users/{id}")
    public void delete(@PathVariable long id, Authentication auth) {
        users.delete(id, accounts.fetchUser(auth.getName()).getId());
    }
}
