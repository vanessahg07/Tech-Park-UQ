package co.edu.uq.techpark.model;

import java.io.Serializable;

/**
 * Rol de un empleado del parque.
 * Define qué funciones puede realizar en el sistema.
 */
public enum RolPersonal implements Serializable {
    /** Puede gestionar atracciones de su zona asignada y procesar ciclos. */
    OPERADOR,
    /** Tiene acceso completo: personal, atracciones, alertas y reportes. */
    ADMINISTRADOR
}
