/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.excepciones;

/**
 *
 * @author ingri
 */
public class LibroYaPrestadoException extends BibliotecaException{
      public LibroYaPrestadoException(String titulo) {
        super("El libro '" + titulo + "' ya está prestado");
    }
}

