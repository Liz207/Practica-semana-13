/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.excepciones;

/**
 *
 * @author ingri
 */
public class LibroNoEncontradoException extends BibliotecaException{
     public LibroNoEncontradoException(String isbn) {
        super("No se encontró el libro con ISBN: " + isbn);
    }
}

