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
public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private String genero;
    private LocalDate fechaPublicacion;
    private EstadoLibro estado;
    private String ubicacion;
    private String notas;

    public Libro(String isbn, String titulo, String autor) {
        validarParametrosObligatorios(isbn, titulo, autor);
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = EstadoLibro.DISPONIBLE;
    }
     private void validarParametrosObligatorios(String isbn, String titulo, String autor) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede ser vacío");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título no puede ser vacío");
        }
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("Autor no puede ser vacío");
        }
    }
     // Getters y Setters con validaciones
    public String getIsbn() { return isbn; }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede ser vacío");
        }
        this.isbn = isbn;
    }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título no puede ser vacío");
        }
        this.titulo = titulo;
    }
public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public EstadoLibro getEstado() { return estado; }
    public void setEstado(EstadoLibro estado) { this.estado = estado; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Libro libro = (Libro) obj;
        return Objects.equals(isbn, libro.isbn);
    }
     @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", titulo, autor, estado);
    }
}

