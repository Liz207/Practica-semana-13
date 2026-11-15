package biblioteca.persistencia;

import biblioteca.dominio.*;
import biblioteca.excepciones.PersistenciaException;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Persistencia JDBC local. H2 evita exigir un SQL Server y cuentas de otra PC. */
public class BibliotecaDAOImpl implements BibliotecaDAO {
    private final String url;
    private final Path respaldo;

    public BibliotecaDAOImpl() throws PersistenciaException {
        this(Paths.get(System.getProperty("biblioteca.directorio", "data")));
    }

    public BibliotecaDAOImpl(Path directorio) throws PersistenciaException {
        try {
            Files.createDirectories(directorio);
            url = "jdbc:h2:" + directorio.toAbsolutePath().resolve("biblioteca");
            respaldo = directorio.toAbsolutePath().resolve("biblioteca-respaldo.sql");
            try (Connection c = conexion(); Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS libros (isbn VARCHAR(100) PRIMARY KEY, "
                        + "titulo VARCHAR(1000) NOT NULL, autor VARCHAR(1000) NOT NULL, genero VARCHAR(500), "
                        + "publicacion DATE, estado VARCHAR(50) NOT NULL, ubicacion VARCHAR(500), notas VARCHAR(4000))");
                s.execute("CREATE TABLE IF NOT EXISTS prestamos (id BIGINT PRIMARY KEY, "
                        + "isbn VARCHAR(100) NOT NULL REFERENCES libros(isbn), prestatario VARCHAR(1000) NOT NULL, "
                        + "fecha DATE NOT NULL, prevista DATE, devuelto DATE, observaciones VARCHAR(4000))");
            }
            sincronizarIds();
        } catch (SQLException | IOException e) {
            throw new PersistenciaException("No se pudo abrir la biblioteca local", e);
        }
    }

    private Connection conexion() throws SQLException { return DriverManager.getConnection(url, "sa", ""); }

    private void sincronizarIds() throws SQLException {
        try (Connection c = conexion(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COALESCE(MAX(id), 0) FROM prestamos")) {
            rs.next();
            Prestamo.asegurarSiguienteId(rs.getLong(1) + 1);
        }
    }

    private void valoresLibro(PreparedStatement s, Libro l) throws SQLException {
        s.setString(1, l.getTitulo()); s.setString(2, l.getAutor()); s.setString(3, l.getGenero());
        s.setObject(4, l.getFechaPublicacion()); s.setString(5, l.getEstado().name());
        s.setString(6, l.getUbicacion()); s.setString(7, l.getNotas()); s.setString(8, l.getIsbn());
    }

    @Override public void guardarLibro(Libro libro) throws PersistenciaException {
        ejecutarLibro("INSERT INTO libros (titulo, autor, genero, publicacion, estado, ubicacion, notas, isbn) VALUES (?,?,?,?,?,?,?,?)", libro);
    }

    @Override public void actualizarLibro(Libro libro) throws PersistenciaException {
        ejecutarLibro("UPDATE libros SET titulo=?,autor=?,genero=?,publicacion=?,estado=?,ubicacion=?,notas=? WHERE isbn=?", libro);
    }

    private void ejecutarLibro(String sql, Libro libro) throws PersistenciaException {
        try (Connection c = conexion(); PreparedStatement s = c.prepareStatement(sql)) {
            valoresLibro(s, libro);
            if (s.executeUpdate() != 1) throw new SQLException("No se encontró el libro " + libro.getIsbn());
        } catch (SQLException e) { throw new PersistenciaException("No se pudo guardar el libro", e); }
    }

    @Override public void eliminarLibro(String isbn) throws PersistenciaException {
        try (Connection c = conexion(); PreparedStatement s = c.prepareStatement("DELETE FROM libros WHERE isbn=?")) {
            s.setString(1, isbn); s.executeUpdate();
        } catch (SQLException e) { throw new PersistenciaException("No se pudo eliminar el libro; revisa si tiene préstamos", e); }
    }

    private Libro leerLibro(ResultSet rs) throws SQLException {
        Libro l = new Libro(rs.getString("isbn"), rs.getString("titulo"), rs.getString("autor"));
        l.setGenero(rs.getString("genero"));
        l.setFechaPublicacion(rs.getObject("publicacion", LocalDate.class));
        l.setEstado(EstadoLibro.valueOf(rs.getString("estado")));
        l.setUbicacion(rs.getString("ubicacion")); l.setNotas(rs.getString("notas"));
        return l;
    }

    @Override public Libro obtenerLibroPorIsbn(String isbn) throws PersistenciaException {
        try (Connection c = conexion(); PreparedStatement s = c.prepareStatement("SELECT * FROM libros WHERE isbn=?")) {
            s.setString(1, isbn);
            try (ResultSet rs = s.executeQuery()) { return rs.next() ? leerLibro(rs) : null; }
        } catch (SQLException e) { throw new PersistenciaException("No se pudo consultar el libro", e); }
    }

    @Override public List<Libro> obtenerTodosLosLibros() throws PersistenciaException {
        List<Libro> libros = new ArrayList<>();
        try (Connection c = conexion(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM libros ORDER BY isbn")) {
            while (rs.next()) libros.add(leerLibro(rs));
            return libros;
        } catch (SQLException e) { throw new PersistenciaException("No se pudieron consultar los libros", e); }
    }

    @Override public void guardarPrestamo(Prestamo prestamo) throws PersistenciaException {
        ejecutarPrestamo("INSERT INTO prestamos (isbn,prestatario,fecha,prevista,devuelto,observaciones,id) VALUES (?,?,?,?,?,?,?)", prestamo);
    }

    @Override public void actualizarPrestamo(Prestamo prestamo) throws PersistenciaException {
        ejecutarPrestamo("UPDATE prestamos SET isbn=?,prestatario=?,fecha=?,prevista=?,devuelto=?,observaciones=? WHERE id=?", prestamo);
    }

    private void ejecutarPrestamo(String sql, Prestamo p) throws PersistenciaException {
        try (Connection c = conexion()) {
            c.setAutoCommit(false);
            try (PreparedStatement s = c.prepareStatement(sql);
                 PreparedStatement libro = c.prepareStatement("UPDATE libros SET estado=? WHERE isbn=?")) {
                s.setString(1, p.getLibro().getIsbn()); s.setString(2, p.getPrestatario());
                s.setObject(3, p.getFechaPrestamo()); s.setObject(4, p.getFechaDevolucionPrevista());
                s.setObject(5, p.getFechaDevolucionReal()); s.setString(6, p.getObservaciones()); s.setLong(7, p.getId());
                if (s.executeUpdate() != 1) throw new SQLException("Préstamo no encontrado");
                libro.setString(1, p.getLibro().getEstado().name()); libro.setString(2, p.getLibro().getIsbn());
                libro.executeUpdate(); c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new PersistenciaException("No se pudo guardar el préstamo", e); }
    }

    @Override public List<Prestamo> obtenerTodosPrestamos() throws PersistenciaException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection c = conexion(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM prestamos ORDER BY id")) {
            while (rs.next()) {
                Libro libro = obtenerLibroPorIsbn(rs.getString("isbn"));
                Prestamo p = new Prestamo(libro, rs.getString("prestatario"), rs.getObject("fecha", LocalDate.class), rs.getObject("prevista", LocalDate.class));
                p.setId(rs.getLong("id")); p.setObservaciones(rs.getString("observaciones"));
                LocalDate devolucion = rs.getObject("devuelto", LocalDate.class);
                if (devolucion != null) p.devolver(devolucion);
                prestamos.add(p);
            }
            return prestamos;
        } catch (SQLException e) { throw new PersistenciaException("No se pudieron consultar los préstamos", e); }
    }

    @Override public void respaldarDatos() throws PersistenciaException {
        try (Connection c = conexion(); Statement s = c.createStatement()) {
            s.execute("SCRIPT TO '" + respaldo.toString().replace("'", "''") + "'");
        } catch (SQLException e) { throw new PersistenciaException("No se pudo crear el respaldo", e); }
    }

    @Override public void restaurarDatos() throws PersistenciaException {
        if (!Files.isRegularFile(respaldo)) throw new PersistenciaException("No existe un respaldo local");
        try (Connection c = conexion(); Statement s = c.createStatement()) {
            // Preparar el script primero permite detectar un respaldo inválido antes
            // de sustituir los datos de la base activa.
            try (Connection prueba = DriverManager.getConnection("jdbc:h2:mem:validar_respaldo", "sa", ""); Statement validar = prueba.createStatement()) {
                validar.execute("RUNSCRIPT FROM '" + respaldo.toString().replace("'", "''") + "'");
            }
            s.execute("DROP ALL OBJECTS");
            s.execute("RUNSCRIPT FROM '" + respaldo.toString().replace("'", "''") + "'");
            sincronizarIds();
        } catch (SQLException e) { throw new PersistenciaException("No se pudo restaurar el respaldo", e); }
    }
}
