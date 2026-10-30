package com.istock.system;

import com.istock.system.entities.User;
import com.istock.system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FrontendIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    record Result(int statusCode, String body) {}

    Result request(String method, String path, String token, String body) throws Exception {
        var builder = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request(HttpMethod.valueOf(method), path)
                .contentType("application/json");
        if (body != null) builder.content(body);
        if (token != null) builder.header("Authorization", "Bearer " + token);
        var response = mvc.perform(builder).andReturn().getResponse();
        return new Result(response.getStatus(), response.getContentAsString());
    }

    String login(String email, String password) throws Exception {
        var response = request("POST", "/api/public/auth/login", null,
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");
        assertEquals(200, response.statusCode(), response.body());
        var match = Pattern.compile("\"accessToken\":\"([^\"]+)\"").matcher(response.body());
        assertTrue(match.find(), response.body());
        return match.group(1);
    }

    @Test
    void jwtLoginPermissionsInventoryAndAccountChanges() throws Exception {
        User admin = new User();
        admin.setName("Admin"); admin.setEmail("integration@example.com");
        admin.setRole("ADMIN"); admin.setPassword(encoder.encode("password123"));
        users.saveAndFlush(admin);
        assertEquals(401, request("GET", "/api/inventory", null, null).statusCode());
        assertEquals(401, request("POST", "/api/public/auth/login", null,
                "{\"email\":\"integration@example.com\",\"password\":\"wrong\"}").statusCode());
        String token = login(admin.getEmail(), "password123");
        var me = request("GET", "/api/auth/me", token, null);
        assertEquals(200, me.statusCode(), me.body());
        assertTrue(me.body().contains("\"role\":\"Admin\""));
        assertFalse(me.body().contains("password"));
        assertEquals(200, request("GET", "/api/admin/users", token, null).statusCode());
        var product = request("POST", "/api/inventory/products", token,
                "{\"name\":\"Pastilha teste\",\"category\":\"Sem categoria\",\"quantity\":10}");
        assertEquals(200, product.statusCode(), product.body());
        var id = Pattern.compile("\"id\":(\\d+)").matcher(product.body());
        assertTrue(id.find());
        String movementPath = "/api/inventory/products/" + id.group(1) + "/movements";
        var moved = request("POST", movementPath, token, "{\"direction\":\"out\",\"quantity\":3,\"version\":0}");
        assertEquals(200, moved.statusCode(), moved.body());
        assertTrue(moved.body().contains("\"quantity\":7"));
        assertEquals(409, request("POST", movementPath, token,
                "{\"direction\":\"out\",\"quantity\":3,\"version\":0}").statusCode());

        var created = request("POST", "/api/admin/users", token,
                "{\"name\":\"Operator\",\"email\":\"operator@example.com\",\"password\":\"password123\",\"role\":\"Operator\",\"active\":true}");
        assertEquals(200, created.statusCode(), created.body());
        String operatorToken = login("operator@example.com", "password123");
        assertEquals(403, request("GET", "/api/admin/users", operatorToken, null).statusCode());
        assertEquals(403, request("GET", "/user", operatorToken, null).statusCode());
        assertEquals(200, request("GET", "/api/inventory", operatorToken, null).statusCode());
        User operator = users.findByEmail("operator@example.com").orElseThrow();
        var update = request("PUT", "/api/admin/users/" + operator.getId(), token,
                "{\"name\":\"Operator\",\"email\":\"operator@example.com\",\"password\":\"newpassword123\",\"role\":\"Operator\",\"active\":true}");
        assertEquals(200, update.statusCode(), update.body());
        login("operator@example.com", "newpassword123");
        operator = users.findById(operator.getId()).orElseThrow();
        operator.setActive(false); users.saveAndFlush(operator);
        assertEquals(401, request("GET", "/api/auth/me", operatorToken, null).statusCode());
        users.delete(operator);
        assertEquals(401, request("GET", "/api/auth/me", operatorToken, null).statusCode());
        assertEquals(401, request("GET", "/api/inventory", "invalid", null).statusCode());
    }
}
