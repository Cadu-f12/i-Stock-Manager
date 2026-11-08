package com.istock.system.security;

import com.istock.system.dtos.request.LoginRequest;
import com.istock.system.dtos.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/public/auth")
@Tag(name = "Authentication", description = "Obtain a JWT access token")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Log in", description = "Authenticate with email and password. Use the returned token as a Bearer token for protected endpoints.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Development seed credentials", required = true,
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "Development administrator",
                    value = "{\"email\":\"admin@istock.com\",\"password\":\"admin\"}")))
    @ApiResponse(responseCode = "200", description = "Authentication succeeded")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        String token = jwtService.generateToken(authentication);

        return ResponseEntity.ok(
                new LoginResponse(token, "Bearer")
        );
    }
}
