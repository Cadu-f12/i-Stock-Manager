package com.istock.system.auth;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Map;
@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    public ResponseEntity<?> staleUpdate() { return ResponseEntity.status(409).body(Map.of("message", "O estoque foi alterado por outra operação. Atualize os dados e tente novamente.")); }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> status(ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", ex.getReason() == null ? "Não foi possível concluir a operação." : ex.getReason())); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict() { return ResponseEntity.status(409).body(Map.of("message", "O cadastro está duplicado ou vinculado a movimentações. Verifique os dados ou desative o usuário.")); }
}
