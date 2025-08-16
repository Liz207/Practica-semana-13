/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.dominio;

/**
 *
 * @author ingri
 */
public enum EstadoLibro {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    PERDIDO("Perdido"),
    DAÑADO("Dañado");

    private final String descripcion;

    EstadoLibro(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
 @Override
    public String toString() {
        return descripcion;
    }
}
