/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.excepciones;

/**
 *
 * @author ingri
 */
public class PersistenciaException extends BibliotecaException{
     public PersistenciaException(String mensaje) {
        super(mensaje);
    }
    
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

