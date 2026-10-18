package com.istock.system;

import com.istock.system.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.net.*;
import java.net.http.*;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:auth-tests;DB_CLOSE_DELAY=-1;NON_KEYWORDS=SYSTEM_USER", "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false"
})
class AuthIntegrationTests {
    @LocalServerPort int port;
    @Autowired UserRepository users;
    HttpClient client() { return HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build(); }
    HttpResponse<String> call(HttpClient client, String method, String path, String body, boolean csrf) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path));
        if (csrf) {
            var tokenBody = call(client, "GET", "/auth/csrf", "", false).body();
            var matcher = Pattern.compile("\"token\":\"([^\"]+)\"").matcher(tokenBody);
            assertTrue(matcher.find()); request.header("X-CSRF-TOKEN", matcher.group(1));
        }
        request.header("Content-Type", path.equals("/auth/login") ? "application/x-www-form-urlencoded" : "application/json");
        request.method(method, HttpRequest.BodyPublishers.ofString(body));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    @Test void loginAndUserLifecycleEnforceServerPermissions() throws Exception {
        var admin = client(); var operator = client(); var anonymous = client();
        assertEquals(401, call(anonymous, "GET", "/admin/users", "", false).statusCode());
        assertEquals(403, call(anonymous, "POST", "/auth/setup", "{}", false).statusCode());
        String initial = "{\"name\":\"Admin\",\"email\":\"admin@example.com\",\"password\":\"TestPass123!\"}";
        assertEquals(200, call(admin, "POST", "/auth/setup", initial, true).statusCode());
        assertEquals(409, call(anonymous, "POST", "/auth/setup", initial, true).statusCode());
        assertNotEquals("TestPass123!", users.findByEmailIgnoreCase("admin@example.com").orElseThrow().getEncryptedPassword());
        assertEquals(401, call(admin, "POST", "/auth/login", "email=admin%40example.com&password=wrong", true).statusCode());
        assertEquals(204, call(admin, "POST", "/auth/login", "email=admin%40example.com&password=TestPass123!", true).statusCode());
        assertEquals(200, call(admin, "GET", "/auth/me", "", false).statusCode());
        String input = "{\"name\":\"Operator\",\"email\":\"operator@example.com\",\"active\":true,\"role\":\"Operador\",\"password\":\"Operator123!\"}";
        var created = call(admin, "POST", "/admin/users", input, true);
        assertEquals(200, created.statusCode(), created.body());
        assertFalse(created.body().contains("password"));
        assertEquals(409, call(admin, "POST", "/admin/users", input, true).statusCode());
        assertEquals(204, call(operator, "POST", "/auth/login", "email=operator%40example.com&password=Operator123!", true).statusCode());
        assertEquals(403, call(operator, "GET", "/admin/users", "", false).statusCode());
        assertEquals(403, call(operator, "POST", "/admin/users", input, true).statusCode());
        var id = users.findByEmailIgnoreCase("operator@example.com").orElseThrow().getId();
        assertEquals(200, call(admin, "PUT", "/admin/users/" + id, input.replace("true", "false"), true).statusCode());
        assertEquals(401, call(operator, "GET", "/auth/me", "", false).statusCode());
        assertEquals(401, call(operator, "POST", "/auth/login", "email=operator%40example.com&password=Operator123!", true).statusCode());
        assertEquals(200, call(admin, "PUT", "/admin/users/" + id, input.replace("Operator123!", "NewPassword123!"), true).statusCode());
        assertEquals(401, call(operator, "POST", "/auth/login", "email=operator%40example.com&password=Operator123!", true).statusCode());
        assertEquals(204, call(operator, "POST", "/auth/login", "email=operator%40example.com&password=NewPassword123!", true).statusCode());
        assertEquals(200, call(admin, "DELETE", "/admin/users/" + id, "", true).statusCode());
        assertEquals(401, call(operator, "GET", "/auth/me", "", false).statusCode());
        long adminId = users.findByEmailIgnoreCase("admin@example.com").orElseThrow().getId();
        assertEquals(409, call(admin, "DELETE", "/admin/users/" + adminId, "", true).statusCode());
        assertEquals(204, call(admin, "POST", "/auth/logout", "", true).statusCode());
        assertEquals(401, call(admin, "GET", "/auth/me", "", false).statusCode());
    }
}
