package com.istock.system.security;
import com.istock.system.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
@Configuration
public class SecurityConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    private static UserDetails principal(com.istock.system.entities.User user) {
        return new CustomUserDetails(user);
    }
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository users) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            .requestMatchers("/api/auth/csrf", "/api/auth/setup", "/api/auth/login").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
            .requestCache(cache -> cache.disable())
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((req, res, ex) -> res.setStatus(401))
                .accessDeniedHandler((req, res, ex) -> res.setStatus(403)))
            .formLogin(form -> form.loginProcessingUrl("/api/auth/login").usernameParameter("email")
                .successHandler((req, res, auth) -> { req.getSession().setAttribute("passwordVersion", users.findById(Long.parseLong(auth.getName())).orElseThrow().getEncryptedPassword()); res.setStatus(204); })
                .failureHandler((req, res, ex) -> res.setStatus(401)))
            .logout(logout -> logout.logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID")
                .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))
            .addFilterAfter(new OncePerRequestFilter() {
                @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof UserDetails details) {
                        var account = users.findById(Long.parseLong(details.getUsername())).orElse(null);
                        if (account == null || !Boolean.TRUE.equals(account.getActive()) || !account.getEncryptedPassword().equals(request.getSession().getAttribute("passwordVersion"))) {
                            SecurityContextHolder.clearContext();
                            if (request.getSession(false) != null) request.getSession(false).invalidate();
                        } else {
                            var fresh = principal(account);
                            var context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(new UsernamePasswordAuthenticationToken(fresh, null, fresh.getAuthorities()));
                            SecurityContextHolder.setContext(context);
                        }
                    }
                    chain.doFilter(request, response);
                }
            }, SecurityContextHolderFilter.class);
        return http.build();
    }
}
