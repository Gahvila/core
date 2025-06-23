package net.gahvila.gahvilacore.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.gahvila.gahvilacore.Config.ConfigManager;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private HikariDataSource dataSource;

    public void connect() {
        if (dataSource != null && !dataSource.isClosed()) return;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigManager.getDatabaseJdbcUrl());
        config.setUsername(ConfigManager.getDatabaseUsername());
        config.setPassword(ConfigManager.getDatabasePassword());
        config.setMaximumPoolSize(ConfigManager.getDatabasePoolSize());

        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            Bukkit.getServer().shutdown();
            throw new IllegalStateException("Database not connected.");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
