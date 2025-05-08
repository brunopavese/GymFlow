package com.uerj.gymflow.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String DB_URL = "jdbc:sqlite:gymflow.db";
    private static HikariDataSource dataSource;
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        initializeDataSource();
    }

    private void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutos
        config.setMaxLifetime(600000); // 10 minutos
        config.setConnectionTimeout(30000); // 30 segundos
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            dataSource = new HikariDataSource(config);
            logger.info("Pool de conexões inicializado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao inicializar pool de conexões", e);
            throw new RuntimeException("Não foi possível inicializar o pool de conexões", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Conexão obtida do pool");
            return connection;
        } catch (SQLException e) {
            logger.error("Erro ao obter conexão do pool", e);
            throw new RuntimeException("Erro ao obter conexão com o banco de dados", e);
        }
    }

    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexões fechado");
        }
    }
}