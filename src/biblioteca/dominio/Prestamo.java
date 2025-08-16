/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.dominio;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author ingri
 */
public class Prestamo {
      private static long contadorId = 1;
    
    private Long id;
    private Libro libro;
    private String prestatario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionPrevista;
    private LocalDate fechaDevolucionReal;
    private String observaciones;

    public Prestamo(Libro libro, String prestatario, LocalDate fechaPrestamo, LocalDate fechaDevolucionPrevista) {
        validarParametrosObligatorios(libro, prestatario, fechaPrestamo);
        this.id = contadorId++;
        this.libro = libro;
        this.prestatario = prestatario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        // Cambiar estado del libro
        libro.setEstado(EstadoLibro.PRESTADO);
    }

    private void validarParametrosObligatorios(Libro libro, String prestatario, LocalDate fechaPrestamo) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro no puede ser null");
        }
        if (prestatario == null || prestatario.trim().isEmpty()) {
            throw new IllegalArgumentException("Prestatario no puede ser vacío");
        }
        if (fechaPrestamo == null) {
            throw new IllegalArgumentException("Fecha de préstamo no puede ser null");
        }
    }

    public void devolver(LocalDate fechaDevolucion) {
        if (fechaDevolucion == null) {
            throw new IllegalArgumentException("Fecha de devolución no puede ser null");
        }
        this.fechaDevolucionReal = fechaDevolucion;
        this.libro.setEstado(EstadoLibro.DISPONIBLE);
    }
 public boolean estaVencido() {
        return fechaDevolucionReal == null && 
               fechaDevolucionPrevista != null && 
               LocalDate.now().isAfter(fechaDevolucionPrevista);
    }

    public boolean estaDevuelto() {
        return fechaDevolucionReal != null;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public Libro getLibro() { return libro; }
    public String getPrestatario() { return prestatario; }
    public void setPrestatario(String prestatario) { this.prestatario = prestatario; }
    
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucionPrevista() { return fechaDevolucionPrevista; }
    public void setFechaDevolucionPrevista(LocalDate fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }
    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Prestamo prestamo = (Prestamo) obj;
        return Objects.equals(id, prestamo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

