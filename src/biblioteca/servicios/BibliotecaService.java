/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.servicios;

import biblioteca.dominio.EstadoLibro;
import biblioteca.dominio.Libro;
import biblioteca.dominio.Prestamo;
import biblioteca.excepciones.BibliotecaException;
import biblioteca.excepciones.LibroNoEncontradoException;
import biblioteca.excepciones.LibroYaPrestadoException;
import biblioteca.excepciones.PersistenciaException;
import biblioteca.persistencia.BibliotecaDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ingri
 */
public class BibliotecaService {
    private final Map<String, Libro> libros; // ISBN -> Libro
    private final List<Prestamo> prestamos;
    private final BibliotecaDAO dao;

    public BibliotecaService(BibliotecaDAO dao) {
        this.libros = new HashMap<>();
        this.prestamos = new ArrayList<>();
        this.dao = dao;
        cargarDatos();
    }
private void cargarDatos() {
        try {
            List<Libro> librosGuardados = dao.obtenerTodosLosLibros();
            for (Libro libro : librosGuardados) {
                libros.put(libro.getIsbn(), libro);
            }
            
            List<Prestamo> prestamosGuardados = dao.obtenerTodosPrestamos();
            prestamos.addAll(prestamosGuardados);
        } catch (PersistenciaException e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }
 public void agregarLibro(Libro libro) throws BibliotecaException {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser null");
        }
        
        if (libros.containsKey(libro.getIsbn())) {
            throw new BibliotecaException("Ya existe un libro con ISBN: " + libro.getIsbn());
        }
        
        libros.put(libro.getIsbn(), libro);
        
        try {
            dao.guardarLibro(libro);
        } catch (PersistenciaException e) {
            libros.remove(libro.getIsbn()); // Rollback
            throw new BibliotecaException("Error al guardar el libro", e);
        }
    }
  public void actualizarLibro(Libro libro) throws BibliotecaException {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser null");
        }
        
        if (!libros.containsKey(libro.getIsbn())) {
            throw new LibroNoEncontradoException(libro.getIsbn());
        }
        
        Libro libroAnterior = libros.get(libro.getIsbn());
        libros.put(libro.getIsbn(), libro);
        
        try {
            dao.actualizarLibro(libro);
        } catch (PersistenciaException e) {
            libros.put(libro.getIsbn(), libroAnterior); // Rollback
            throw new BibliotecaException("Error al actualizar el libro", e);
        }
    } 
  public void eliminarLibro(String isbn) throws BibliotecaException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede ser vacío");
        }
        
        Libro libro = libros.get(isbn);
        if (libro == null) {
            throw new LibroNoEncontradoException(isbn);
        }
        
        // Verificar si tiene préstamos activos
        boolean tienePrestamoActivo = prestamos.stream()
                .anyMatch(p -> p.getLibro().getIsbn().equals(isbn) && !p.estaDevuelto());
        
        if (tienePrestamoActivo) {
            throw new BibliotecaException("No se puede eliminar el libro porque tiene un préstamo activo");
        }
        
        libros.remove(isbn);
  try {
            dao.eliminarLibro(isbn);
        } catch (PersistenciaException e) {
            libros.put(isbn, libro); // Rollback
            throw new BibliotecaException("Error al eliminar el libro", e);
        }
    }

    public Libro buscarLibroPorIsbn(String isbn) throws LibroNoEncontradoException {
        Libro libro = libros.get(isbn);
        if (libro == null) {
            throw new LibroNoEncontradoException(isbn);
        }
        return libro;
    }

    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        return libros.values().stream()
                .filter(libro -> libro.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Libro> buscarLibrosPorAutor(String autor) {
        return libros.values().stream()
                .filter(libro -> libro.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .collect(Collectors.toList());
    }
    
     public List<Libro> obtenerTodosLosLibros() {
        return new ArrayList<>(libros.values());
    }

    public List<Libro> obtenerLibrosDisponibles() {
        return libros.values().stream()
                .filter(libro -> libro.getEstado() == EstadoLibro.DISPONIBLE)
                .collect(Collectors.toList());
    }

    public Prestamo prestarLibro(String isbn, String prestatario, LocalDate fechaDevolucion) 
            throws BibliotecaException {
        
        Libro libro = buscarLibroPorIsbn(isbn);
        
        if (libro.getEstado() != EstadoLibro.DISPONIBLE) {
            throw new LibroYaPrestadoException(libro.getTitulo());
        }
        
        Prestamo prestamo = new Prestamo(libro, prestatario, LocalDate.now(), fechaDevolucion);
        prestamos.add(prestamo);
         try {
            dao.guardarPrestamo(prestamo);
            dao.actualizarLibro(libro); // Actualizar estado del libro
        } catch (PersistenciaException e) {
            prestamos.remove(prestamo); // Rollback
            libro.setEstado(EstadoLibro.DISPONIBLE); // Rollback estado
            throw new BibliotecaException("Error al registrar el préstamo", e);
        }
        
        return prestamo;
    }

    public void devolverLibro(Long prestamoId, LocalDate fechaDevolucion) throws BibliotecaException {
        Prestamo prestamo = prestamos.stream()
                .filter(p -> p.getId().equals(prestamoId))
                .findFirst()
                .orElseThrow(() -> new BibliotecaException("No se encontró el préstamo con ID: " + prestamoId));
        
        if (prestamo.estaDevuelto()) {
            throw new BibliotecaException("El libro ya fue devuelto");
        }
        
        prestamo.devolver(fechaDevolucion);
         try {
            dao.actualizarPrestamo(prestamo);
            dao.actualizarLibro(prestamo.getLibro());
        } catch (PersistenciaException e) {
            throw new BibliotecaException("Error al registrar la devolución", e);
        }
    }

    public List<Prestamo> obtenerTodosPrestamos() {
        return new ArrayList<>(prestamos);
    }

    public List<Prestamo> obtenerPrestamosActivos() {
        return prestamos.stream()
                .filter(prestamo -> !prestamo.estaDevuelto())
                .collect(Collectors.toList());
    }

    public List<Prestamo> obtenerPrestamosVencidos() {
        return prestamos.stream()
                .filter(Prestamo::estaVencido)
                .collect(Collectors.toList());
    }
}

