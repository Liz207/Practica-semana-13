/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.persistencia;

import biblioteca.dominio.Libro;
import biblioteca.dominio.Prestamo;
import biblioteca.excepciones.PersistenciaException;
import java.util.List;

/**
 *
 * @author ingri
 */
public interface BibliotecaDAO {
    
    // Operaciones de Libro
    void guardarLibro(Libro libro) throws PersistenciaException;
    void actualizarLibro(Libro libro) throws PersistenciaException;
    void eliminarLibro(String isbn) throws PersistenciaException;
    Libro obtenerLibroPorIsbn(String isbn) throws PersistenciaException;
    List<Libro> obtenerTodosLosLibros() throws PersistenciaException;
    
    // Operaciones de Préstamo
    void guardarPrestamo(Prestamo prestamo) throws PersistenciaException;
    void actualizarPrestamo(Prestamo prestamo) throws PersistenciaException;
    List<Prestamo> obtenerTodosPrestamos() throws PersistenciaException;
    
    // Operaciones de respaldo
    void respaldarDatos() throws PersistenciaException;
    void restaurarDatos() throws PersistenciaException;
}
