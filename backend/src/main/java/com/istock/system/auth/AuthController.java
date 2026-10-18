package com.istock.system.auth;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService users;
    public AuthController(UserService users) { this.users = users; }
    @GetMapping("/auth/csrf") public Map<String, String> csrf(CsrfToken token) { return Map.of("token", token.getToken(), "headerName", token.getHeaderName()); }
    @GetMapping("/auth/setup") public Map<String, Boolean> setupStatus() { return Map.of("required", users.needsSetup()); }
    @PostMapping("/auth/setup") public UserService.UserView setup(@RequestBody UserService.UserInput input) { return users.setup(input); }
    @GetMapping("/auth/me") public UserService.UserView me(Authentication auth) { return UserService.UserView.of(users.find(Long.parseLong(auth.getName()))); }
    @GetMapping("/admin/users") public java.util.List<UserService.UserView> list() { return users.list(); }
    @PostMapping("/admin/users") public UserService.UserView create(@RequestBody UserService.UserInput input, Authentication auth) { return users.save(null, input, Long.parseLong(auth.getName())); }
    @PutMapping("/admin/users/{id}") public UserService.UserView update(@PathVariable long id, @RequestBody UserService.UserInput input, Authentication auth) { return users.save(id, input, Long.parseLong(auth.getName())); }
    @DeleteMapping("/admin/users/{id}") public void delete(@PathVariable long id, Authentication auth) { users.delete(id, Long.parseLong(auth.getName())); }
}
