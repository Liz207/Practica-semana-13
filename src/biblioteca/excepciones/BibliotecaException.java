/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.excepciones;

/**
 *
 * @author ingri
 */
public class BibliotecaException extends Exception{
     public BibliotecaException(String mensaje) {
        super(mensaje);
    }
    
    public BibliotecaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

