/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.persistencia;
import biblioteca.dominio.*;
import biblioteca.excepciones.PersistenciaException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaDAOImpl implements BibliotecaDAO {
    
    // 📌 Cambia los valores según tu SQL Server
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=BibliotecaDB;encrypt=false;trustServerCertificate=true;";
private static final String DB_USER = "bibliotecaUser";
private static final String DB_PASSWORD = "biblioteca123";

    public BibliotecaDAOImpl() throws PersistenciaException {
        inicializarBaseDatos();
    }
    
    private void inicializarBaseDatos() throws PersistenciaException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (conn != null) {
                crearTablas(conn);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("❌ Error al inicializar la base de datos en SQL Server", e);
        }
    }

    private void crearTablas(Connection conn) throws SQLException {
        String sqlLibros = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Libros' AND xtype='U') " +
                "CREATE TABLE Libros ("
                + "id INT PRIMARY KEY IDENTITY(1,1),"
                + "titulo NVARCHAR(255) NOT NULL,"
                + "autor NVARCHAR(255) NOT NULL,"
                + "estado NVARCHAR(50) NOT NULL"
                + ");";

        String sqlPrestamos = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Prestamos' AND xtype='U') " +
                "CREATE TABLE Prestamos ("
                + "id INT PRIMARY KEY IDENTITY(1,1),"
                + "libro_id INT NOT NULL,"
                + "fecha_prestamo DATE NOT NULL,"
                + "fecha_devolucion DATE,"
                + "FOREIGN KEY(libro_id) REFERENCES Libros(id)"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLibros);
            stmt.execute(sqlPrestamos);
        }
    }

    // ----------------- Ejemplo de métodos -----------------
    
    public void agregarLibro(Libro libro) throws PersistenciaException {
        String sql = "INSERT INTO Libros (titulo, autor, estado) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getEstado().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("❌ Error al agregar libro", e);
        }
    }

    public List<Libro> obtenerLibros() throws PersistenciaException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM Libros";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        EstadoLibro.valueOf(rs.getString("estado"))
                );
                libros.add(libro);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("❌ Error al obtener libros", e);
        }
        return libros;
    }
}
