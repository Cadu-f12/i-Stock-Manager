package com.istock.system.schema;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Component
@Order(0)
public class MovementReasonSchemaUpdate implements CommandLineRunner {
    private final DataSource dataSource;

    public MovementReasonSchemaUpdate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            try (var column = statement.executeQuery("SELECT reason_movement_id FROM movement WHERE 1 = 0")) {
                if (column.getMetaData().isNullable(1) == ResultSetMetaData.columnNullable) return;
            }
            String sql = switch (connection.getMetaData().getDatabaseProductName()) {
                case "MySQL" -> "ALTER TABLE movement MODIFY COLUMN reason_movement_id BIGINT NULL";
                case "H2" -> "ALTER TABLE movement ALTER COLUMN reason_movement_id DROP NOT NULL";
                default -> throw new SQLException("Unsupported database for movement reason schema update.");
            };
            statement.executeUpdate(sql);
        }
    }
}
