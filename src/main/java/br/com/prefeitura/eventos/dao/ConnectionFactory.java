package br.com.prefeitura.eventos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    // Credenciais que definimos no docker-compose.yml
    private static final String URL = "jdbc:postgresql://localhost:5432/prefeitura_eventos";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminpassword";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
        }
    }
}