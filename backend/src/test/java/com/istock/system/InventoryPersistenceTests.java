package com.istock.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.net.*;
import java.net.http.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

class InventoryPersistenceTests {
    @TempDir Path directory;
    private int port;
    private final JsonMapper json = new JsonMapper();
    private ConfigurableApplicationContext start() {
        var context = SpringApplication.run(BackendApplication.class,
            "--server.port=0", "--spring.datasource.url=jdbc:h2:file:" + directory.resolve("inventory").toString().replace('\\', '/') + ";NON_KEYWORDS=SYSTEM_USER",
            "--spring.datasource.driver-class-name=org.h2.Driver", "--spring.datasource.username=sa", "--spring.datasource.password=",
            "--spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect", "--spring.jpa.hibernate.ddl-auto=update",
            "--spring.jpa.show-sql=false", "--logging.level.root=ERROR", "--debug=false");
        port = ((WebServerApplicationContext) context).getWebServer().getPort();
        return context;
    }
    private HttpClient client() { return HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build(); }
    private HttpResponse<String> call(HttpClient client, String method, String path, String body, boolean csrf) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path));
        if (csrf) {
            var token = json.readTree(call(client, "GET", "/auth/csrf", "", false).body());
            request.header(token.get("headerName").asText(), token.get("token").asText());
        }
        request.header("Content-Type", path.equals("/auth/login") ? "application/x-www-form-urlencoded" : "application/json");
        return client.send(request.method(method, HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
    private JsonNode ok(HttpResponse<String> response) {
        assertEquals(200, response.statusCode(), response.body());
        return json.readTree(response.body());
    }
    private HttpClient login() throws Exception {
        var client = client();
        assertEquals(204, call(client, "POST", "/auth/login", "email=inventory%40example.com&password=TestPass123!", true).statusCode());
        return client;
    }
    private String movement(int quantity, long version) {
        return "{\"direction\":\"out\",\"quantity\":" + quantity + ",\"version\":" + version + "}";
    }
    @Test void inventoryAndHistorySurviveRestartAndRejectConcurrentOrInvalidWrites() throws Exception {
        long productId;
        try (var context = start()) {
            var anonymous = client();
            assertEquals(401, call(anonymous, "GET", "/inventory", "", false).statusCode());
            ok(call(anonymous, "POST", "/auth/setup", "{\"name\":\"Inventory Admin\",\"email\":\"inventory@example.com\",\"password\":\"TestPass123!\"}", true));
            var first = login();
            assertEquals(403, call(first, "POST", "/inventory/products", "{}", false).statusCode());
            ok(call(first, "POST", "/inventory/categories", "{\"name\":\"Tools\"}", true));
            assertEquals(409, call(first, "POST", "/inventory/categories", "{\"name\":\"tools\"}", true).statusCode());
            var product = ok(call(first, "POST", "/inventory/products", "{\"name\":\"Drill\",\"category\":\"Tools\",\"quantity\":5}", true));
            productId = product.get("id").asLong();
            String path = "/inventory/products/" + productId;
            product = ok(call(first, "PUT", path, "{\"name\":\"Updated drill\",\"category\":\"Tools\",\"quantity\":7,\"version\":" + product.get("version").asLong() + "}", true));
            assertEquals("Tools", product.get("category").asText(), "Editing quantity must preserve category");
            product = ok(call(first, "POST", path + "/movements", "{\"direction\":\"in\",\"quantity\":3,\"version\":" + product.get("version").asLong() + "}", true));
            assertEquals("Tools", product.get("category").asText(), "Movement response must include the category");
            assertEquals(10, product.get("quantity").asInt());
            long version = product.get("version").asLong();
            var second = login();
            var requests = List.of(first, second).stream().map(client -> CompletableFuture.supplyAsync(() -> {
                try {
                    var response = call(client, "POST", path + "/movements", movement(4, version), true);
                    if (response.statusCode() == 200) assertEquals("Tools", json.readTree(response.body()).get("category").asText());
                    return response.statusCode();
                }
                catch (Exception ex) { throw new RuntimeException(ex); }
            })).toList();
            assertEquals(List.of(200, 409), requests.stream().map(CompletableFuture::join).sorted().toList());
            product = ok(call(second, "GET", "/inventory", "", false)).get("products").get(0);
            assertEquals(6, product.get("quantity").asInt());
            long currentVersion = product.get("version").asLong();
            assertEquals(409, call(first, "POST", path + "/movements", movement(7, currentVersion), true).statusCode());
            assertEquals(400, call(first, "POST", path + "/movements", movement(0, currentVersion), true).statusCode());
            assertEquals(400, call(first, "POST", "/inventory/products", "{\"name\":\"Invalid\",\"category\":\"Tools\",\"quantity\":-1}", true).statusCode());
            assertEquals(400, call(first, "POST", "/inventory/products", "{\"name\":\"Invalid\",\"category\":\"Tools\",\"quantity\":1.5}", true).statusCode());
            assertEquals(4, product.get("history").size());
            assertEquals("Inventory Admin", product.get("history").get(0).get("author").asText());
        }
        // A new application context, JDBC connection and HTTP session read the same on-disk database.
        try (var context = start()) {
            var client = login();
            var inventory = ok(call(client, "GET", "/inventory", "", false));
            var product = inventory.get("products").get(0);
            assertEquals(productId, product.get("id").asLong());
            assertEquals("Updated drill", product.get("name").asText());
            assertEquals("Tools", product.get("category").asText());
            assertEquals(6, product.get("quantity").asInt());
            assertEquals(4, product.get("history").size());
            ok(call(client, "DELETE", "/inventory/categories?name=Tools", "", true));
            inventory = ok(call(client, "GET", "/inventory", "", false));
            assertEquals(1, inventory.get("categories").size());
            assertEquals("Sem categoria", inventory.get("products").get(0).get("category").asText());
            assertEquals(5, inventory.get("products").get(0).get("history").size());
            assertEquals(6, inventory.get("products").get(0).get("quantity").asInt());
        }
    }
}
