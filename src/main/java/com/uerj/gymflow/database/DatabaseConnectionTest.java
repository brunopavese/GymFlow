package com.uerj.gymflow.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionTest {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);

    public static void main(String[] args) {
        testConnection();
    }

    public static void testConnection() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            logger.info("Testando conexão com o banco de dados...");

            // Testa metadados do banco
            logger.info("Informações do banco de dados:");
            logger.info("Driver: {}", conn.getMetaData().getDriverName());
            logger.info("URL: {}", conn.getMetaData().getURL());

            // Lista todas as tabelas
            try (ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {
                logger.info("Tabelas encontradas:");
                while (rs.next()) {
                    logger.info("- {}", rs.getString("name"));
                }
            }

            logger.info("Teste de conexão concluído com sucesso!");

        } catch (SQLException e) {
            logger.error("Erro durante teste de conexão", e);
            throw new RuntimeException("Falha no teste de conexão", e);
        }
    }
}