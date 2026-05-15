package co.edu.uq.techpark.util;

/**
 * Excepción de negocio del parque.
 * Se lanza cuando una operación no puede completarse por una regla del negocio,
 * por ejemplo: aforo completo, saldo insuficiente, atracción cerrada, etc.
 */
public class ExcepcionDelParque extends RuntimeException {

    public ExcepcionDelParque(String mensaje) {
        super(mensaje);
    }

    public ExcepcionDelParque(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
