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
    @Autowired com.istock.system.repository.ManufacturerRepository manufacturers;
    @Autowired com.istock.system.repository.SupplierRepository suppliers;
    @Autowired tools.jackson.databind.ObjectMapper json;
    @Autowired com.istock.system.schema.MovementReasonSchemaUpdate movementSchema;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
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
        String legacyPath = "/user/updateUser/" + operator.getId();
        String legacyFields = "\"name\":\"Operator\",\"email\":\"operator@example.com\"";
        assertEquals(200, request("PUT", legacyPath, token, "{" + legacyFields + "}").statusCode());
        login(operator.getEmail(), "password123");
        assertEquals(200, request("PUT", legacyPath, token,
                "{" + legacyFields + ",\"password\":\"changed123\"}").statusCode());
        login(operator.getEmail(), "changed123");
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

    @Test
    void customReasonLifecycleAndPastilleDeletion() throws Exception {
        User operator = new User();
        operator.setName("Stock operator");
        operator.setEmail("stock-lifecycle@example.com");
        operator.setRole("OPERATOR");
        operator.setPassword(encoder.encode("password123"));
        users.saveAndFlush(operator);
        String token = login(operator.getEmail(), "password123");
        String reasons = "/reason-movement";
        assertEquals(401, request("GET", reasons, null, null).statusCode());
        assertEquals(401, request("DELETE", "/pastilles/1", null, null).statusCode());
        assertEquals(400, request("POST", reasons, token, "{}").statusCode());
        var created = request("POST", reasons, token, """
                {"description":"Custom purchase","type":"IN"}
                """);
        assertEquals(201, created.statusCode(), created.body());
        long reasonId = json.readTree(created.body()).get("id").asLong();
        String reason = reasons + "/" + reasonId;
        var invalid = request("PUT", reason, token, """
                {"description":"Custom purchase"}
                """);
        assertEquals(400, invalid.statusCode());
        assertTrue(invalid.body().contains("is_active"));
        var maker = manufacturers.save(new com.istock.system.entities.Manufacturer(null, null, "Lifecycle maker", "Contact", true));
        var vendor = suppliers.save(new com.istock.system.entities.Supplier(null, "Lifecycle vendor", "Contact", true));
        String catalog = "/pastilles/categories";
        var category = request("POST", catalog, token, """
                {"name":"Lifecycle category"}
                """);
        assertEquals(201, category.statusCode());
        long categoryId = json.readTree(category.body()).get("id").asLong();
        String deleteCategory = catalog + "/" + categoryId;
        assertEquals(401, request("DELETE", deleteCategory, null, null).statusCode());
        var product = request("POST", "/pastilles", token, """
                {"sku_code":"LIFECYCLE-TEST","description":"Insert","minimum_stock_level":1,"manufacturer_id":%d,"supplier_ids":[%d],"category":{"id":%d}}
                """.formatted(maker.getId(), vendor.getId(), categoryId));
        assertEquals(201, product.statusCode(), product.body());
        long id = json.readTree(product.body()).get("id").asLong();
        assertEquals(409, request("DELETE", deleteCategory, token, null).statusCode());
        var rows = json.readTree(request("GET", catalog, token, null).body());
        var listed = rows.valueStream().filter(c -> c.get("id").asLong() == categoryId).findFirst().orElseThrow();
        assertEquals(1, listed.get("pastilleCount").asLong());
        String deleteMaker = "/manufacturer/deleteManufacturer/" + maker.getId();
        String deleteVendor = "/supplier/deleteSupplier/" + vendor.getId();
        assertEquals(409, request("DELETE", deleteMaker, token, null).statusCode());
        assertEquals(409, request("DELETE", deleteVendor, token, null).statusCode());
        String customEntry = """
                {"pastille":%d,"custom_reason":"  Retorno da bancada  ","quantity":2}
                """.formatted(id);
        jdbc.execute("ALTER TABLE movement ALTER COLUMN reason_movement_id SET NOT NULL");
        var conflict = request("POST", "/movement", token, customEntry);
        assertEquals(409, conflict.statusCode());
        assertEquals("MOVEMENT_CONFLICT", json.readTree(conflict.body()).get("error").asText());
        assertEquals(0, jdbc.queryForObject("SELECT current_balance FROM pastilles WHERE id = ?", Integer.class, id));
        movementSchema.run();
        movementSchema.run();
        var singleUse = request("POST", "/movement", token, customEntry);
        assertEquals(201, singleUse.statusCode(), singleUse.body());
        var savedReason = json.readTree(singleUse.body()).get("reason");
        assertEquals("Retorno da bancada", savedReason.get("description").asText());
        assertEquals("IN", savedReason.get("type").asText());
        assertTrue(savedReason.get("id").isNull());
        assertTrue(request("GET", "/movement?type=IN&pastille_id=" + id, token, null).body().contains("Retorno da bancada"));
        assertEquals("[]", request("GET", "/movement?type=OUT&pastille_id=" + id, token, null).body());
        assertFalse(request("GET", reasons, token, null).body().contains("Retorno da bancada"));
        for (String invalidFields : new String[] { "", ",\"custom_reason\":\"   \"", ",\"custom_reason\":\"x\",\"reason_movement_id\":" + reasonId }) {
            assertEquals(400, request("POST", "/movement", token,
                    "{\"pastille\":" + id + ",\"quantity\":2" + invalidFields + "}").statusCode());
        }
        String movement = """
                {"pastille":%d,"reason_movement_id":%d,"quantity":2}
                """.formatted(id, reasonId);
        assertEquals(201, request("POST", "/movement", token, movement).statusCode());
        assertEquals(204, request("DELETE", reason, token, null).statusCode());
        assertEquals(404, request("POST", "/movement", token, movement).statusCode());
        assertTrue(request("GET", reasons + "?is_active=false", token, null).body().contains("Custom purchase"));
        assertFalse(request("GET", reasons + "?is_active=true", token, null).body().contains("Custom purchase"));
        String history = "/movement?pastille_id=" + id;
        assertTrue(request("GET", history, token, null).body().contains("Custom purchase"));
        assertEquals(200, request("PUT", reason, token, """
                {"description":"Renamed purchase","is_active":true}
                """).statusCode());
        assertTrue(request("GET", "/movement/reasons", token, null).body().contains("Renamed purchase"));
        assertEquals(201, request("POST", "/movement", token, movement).statusCode());
        assertEquals(204, request("DELETE", "/pastilles/" + id, token, null).statusCode());
        assertEquals(404, request("GET", "/pastilles/" + id, token, null).statusCode());
        assertEquals("[]", request("GET", history, token, null).body());
        assertEquals(200, request("GET", reason, token, null).statusCode());
        assertTrue(suppliers.existsById(vendor.getId()));
        assertTrue(manufacturers.existsById(maker.getId()));
        var deletedCategory = request("DELETE", deleteCategory, token, null);
        assertEquals(200, deletedCategory.statusCode());
        assertEquals(categoryId, json.readTree(deletedCategory.body()).get("id").asLong());
        assertEquals(404, request("DELETE", deleteCategory, token, null).statusCode());
        assertEquals(200, request("DELETE", deleteMaker, token, null).statusCode());
        assertEquals(200, request("DELETE", deleteVendor, token, null).statusCode());
        assertFalse(manufacturers.existsById(maker.getId()));
        assertFalse(suppliers.existsById(vendor.getId()));
        assertEquals(404, request("DELETE", "/pastilles/" + id, token, null).statusCode());
        assertEquals(404, request("DELETE", reasons + "/" + Long.MAX_VALUE, token, null).statusCode());
    }
}
