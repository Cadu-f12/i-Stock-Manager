package com.istock.system.auth;
import com.istock.system.entities.User;
import com.istock.system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.List;

@Service
public class UserService {
    public record UserView(Long id, String name, String email, boolean active, String role) {
        public static UserView of(User u) { return new UserView(u.getId(), u.getName(), u.getEmail(), Boolean.TRUE.equals(u.getActive()), u.getRole()); }
    }
    public record UserInput(String name, String email, Boolean active, String role, String password) {}
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TransactionTemplate transactions;
    public UserService(UserRepository users, PasswordEncoder encoder, TransactionTemplate transactions) {
        this.users = users; this.encoder = encoder; this.transactions = transactions;
    }
    public List<UserView> list() { return users.findAll().stream().map(UserView::of).toList(); }
    public User find(long id) { return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")); }
    public boolean needsSetup() { return users.count() == 0; }
    public synchronized UserView setup(UserInput input) {
        return transactions.execute(status -> {
            if (!needsSetup()) throw new ResponseStatusException(HttpStatus.CONFLICT, "O administrador inicial já foi cadastrado.");
            return write(null, new UserInput(input.name(), input.email(), true, "Admin", input.password()));
        });
    }
    public synchronized UserView save(Long id, UserInput input, long actorId) {
        return transactions.execute(status -> {
            if (id != null && id == actorId && (!Boolean.TRUE.equals(input.active()) || !"Admin".equals(input.role())))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Você não pode desativar sua própria conta ou remover seu papel de Admin.");
            return write(id, input);
        });
    }
    private UserView write(Long id, UserInput input) {
        String name = input.name() == null ? "" : input.name().trim();
        String email = input.email() == null ? "" : input.email().trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty() || name.length() > 120 || email.length() > 60 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || input.active() == null || !("Admin".equals(input.role()) || "Operator".equals(input.role())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe nome, e-mail, status e papel válidos.");
        users.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
        });
        User user = id == null ? new User() : find(id);
        String password = input.password();
        if (id == null || (password != null && !password.isEmpty())) {
            if (password == null || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha deve ter ao menos 8 caracteres e no máximo 72 bytes.");
            user.setEncryptedPassword(encoder.encode(password));
        }
        user.setName(name); user.setEmail(email); user.setActive(input.active()); user.setRole(input.role());
        return UserView.of(users.saveAndFlush(user));
    }
    public synchronized void delete(long id, long actorId) {
        if (id == actorId) throw new ResponseStatusException(HttpStatus.CONFLICT, "Você não pode excluir sua própria conta.");
        transactions.executeWithoutResult(status -> users.delete(find(id)));
    }
}
