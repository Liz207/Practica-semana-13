import biblioteca.dominio.*;
import biblioteca.persistencia.*;
import biblioteca.excepciones.PersistenciaException;
import java.nio.file.*;
import java.time.LocalDate;

public class SmokeBiblioteca {
    private static void check(boolean ok, String description) {
        if (!ok) throw new AssertionError(description);
        System.out.println("PASS: " + description);
    }
    public static void main(String[] args) throws Exception {
        Path data = Paths.get(args[0]);
        BibliotecaDAOImpl dao = new BibliotecaDAOImpl(data);
        check(dao.obtenerTodosLosLibros().isEmpty(), "base nueva vacía");
        Libro libro = new Libro("978-1", "Título con ñ", "Autor");
        dao.guardarLibro(libro);
        check(dao.obtenerLibroPorIsbn("978-1").getTitulo().equals("Título con ñ"), "alta y lectura Unicode");
        libro.setTitulo("Título corregido");
        dao.actualizarLibro(libro);
        check(dao.obtenerTodosLosLibros().size() == 1, "actualización sin duplicar");
        Prestamo prestamo = new Prestamo(libro, "Lector", LocalDate.now(), LocalDate.now().plusDays(7));
        dao.guardarPrestamo(prestamo);
        check(dao.obtenerLibroPorIsbn("978-1").getEstado() == EstadoLibro.PRESTADO, "estado prestado persistido");
        BibliotecaDAOImpl reabierta = new BibliotecaDAOImpl(data);
        Prestamo recuperado = reabierta.obtenerTodosPrestamos().get(0);
        check(recuperado.getId().equals(prestamo.getId()), "ID estable al reabrir");
        recuperado.devolver(LocalDate.now());
        reabierta.actualizarPrestamo(recuperado);
        check(reabierta.obtenerLibroPorIsbn("978-1").getEstado() == EstadoLibro.DISPONIBLE, "devolución persistida");
        reabierta.respaldarDatos();
        Libro segundo = new Libro("978-2", "Otro", "Autor");
        reabierta.guardarLibro(segundo);
        check(reabierta.obtenerTodosLosLibros().size() == 2, "segunda alta");
        reabierta.restaurarDatos();
        check(reabierta.obtenerTodosLosLibros().size() == 1, "restauración del respaldo");
        check(reabierta.obtenerTodosPrestamos().get(0).estaDevuelto(), "préstamos incluidos en respaldo");
        reabierta.guardarLibro(segundo);
        reabierta.eliminarLibro("978-2");
        check(reabierta.obtenerLibroPorIsbn("978-2") == null, "baja de libro sin préstamos");
        try {
            reabierta.eliminarLibro("978-1");
            throw new AssertionError("debe proteger el historial de préstamos");
        } catch (PersistenciaException esperado) { System.out.println("PASS: integridad referencial"); }
        check(reabierta.obtenerLibroPorIsbn("' OR 1=1 --") == null, "ISBN se trata como dato");
    }
}
