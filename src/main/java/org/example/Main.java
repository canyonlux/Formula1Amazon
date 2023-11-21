package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://databaseformula1.cdenajzkqjrp.us-east-1.rds.amazonaws.com:5432/formula1";
        String user = "postgres";
        String password = "1029384756";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);

            insertarEquipo(connection);
            // Asumiendo que obtenemos el ID del equipo...
            int equipoId = obtenerEquipoId(connection, "Seat F1");

            insertarPiloto(connection, "Carlos Sainz", "CSZ", equipoId);
            insertarPiloto(connection, "Manuel Aloma", "ALM", equipoId);

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void insertarEquipo(Connection connection) throws SQLException {
        String sql = "INSERT INTO equipos (nombre) VALUES ('Seat F1') " +
                "ON CONFLICT (id) DO NOTHING RETURNING id;";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int equipoId = rs.getInt(1);
                // Usar equipoId para operaciones futuras...
            }
        }
    }
    private static void insertarPiloto(Connection connection, String nombre, String codigo, int equipoId) throws SQLException {
        String sql = "INSERT INTO pilotos (nombre, codigo, equipo_id) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, codigo);
            pstmt.setInt(3, equipoId);
            pstmt.executeUpdate();
        }
    }
}
