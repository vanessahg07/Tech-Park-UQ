package co.edu.uq.techpark.util;

/** Excepción lanzada cuando falla la autenticación de un usuario. Requisitos: 12.1, 12.2 */
public class ExcepcionDeAutenticacion extends ExcepcionDelParque {
    public ExcepcionDeAutenticacion(String mensaje) {
        super(mensaje);
    }
}
