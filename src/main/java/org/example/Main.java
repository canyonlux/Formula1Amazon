package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        // Datos de conexión a la base de datos
        String dbUrl = "jdbc:postgresql://databaseformula1.cdenajzkqjrp.us-east-1.rds.amazonaws.com:5432/formula1";
        String user = "postgres";
        String password = "1029384756";

        Connection connection = null;
        try {
            // Estableciendo la conexión con la base de datos
            connection = DriverManager.getConnection(dbUrl, user, password);
            System.out.println("Conexion realizada");
            // Desactivando auto-commit para manejar la transacción manualmente
            connection.setAutoCommit(false);

            // Datos del equipo
            String constructorRef = "seat_f1"; // Ajusta estos valores según tus necesidades
            String name = "Seat F1";
            String nationality = "Española";
            String teamUrl = "http://ejemplo.com/seat_f1";


            // Insertar equipo
            int equipoId = insertarEquipo(connection, constructorRef, name, nationality, teamUrl);
            if (equipoId == -1) {
                // Si el equipo ya existe, obtener su ID
                equipoId = obtenerEquipoId(connection, name);
            }
            Date dobCarlos = Date.valueOf("1994-09-01"); // Fecha de nacimiento de Carlos Sainz
            String urlCarlos = "http://ejemplo.com/carlos_sainz";

            Date dobManuel = Date.valueOf("1990-05-20"); // Fecha de nacimiento de Manuel Aloma
            String urlManuel = "http://ejemplo.com/manuel_aloma";

            // Insertar el primer piloto
            insertarPiloto(connection, "CSZ", "Carlos", "Sainz", dobCarlos, "Española", equipoId, urlCarlos);

            // Insertar el segundo piloto
            insertarPiloto(connection, "ALM", "Manuel", "Aloma", dobManuel, "Española", equipoId, urlManuel);


            connection.commit();
        } catch (SQLException e) {
            // En caso de error, revertir los cambios
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // Cerrar la conexión
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static int insertarEquipo(Connection connection, String constructorRef, String name, String nationality, String url) throws SQLException {
        // SQL para insertar un equipo con UPSERT
        String sql = "INSERT INTO constructors (constructorref, name, nationality, url) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (constructorref) DO NOTHING RETURNING constructorid;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, constructorRef);
            stmt.setString(2, name);
            stmt.setString(3, nationality);
            stmt.setString(4, url);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Retorna el ID del constructor insertado
                return rs.getInt(1);  // Aquí se corrige para obtener el primer valor del ResultSet
            } else {
                System.out.println("El constructor ya existe y no se insertó.");
                return -1; // Retorna -1 si el constructor ya existe
            }
        }
    }


    private static int obtenerEquipoId(Connection connection, String equipoNombre) throws SQLException {
        // Corrección en el nombre de la columna: de 'nombre' a 'name'
        String sql = "SELECT constructorid FROM constructors WHERE name = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, equipoNombre);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("constructorid");
            } else {
                throw new SQLException("Equipo no encontrado");
            }
        }
    }
    private static void insertarPiloto(Connection connection, String code, String forename, String surname, Date dob, String nationality, int constructorId, String url) throws SQLException {
        // SQL para insertar un piloto
        String sql = "INSERT INTO drivers (code, forename, surname, dob, nationality, constructorid, url) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, forename);
            pstmt.setString(3, surname);
            pstmt.setDate(4, dob);
            pstmt.setString(5, nationality);
            pstmt.setInt(6, constructorId);
            pstmt.setString(7, url);
            pstmt.executeUpdate();
        }
    }





}
