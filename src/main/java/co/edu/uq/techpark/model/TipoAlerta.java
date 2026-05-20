package co.edu.uq.techpark.model;

import java.io.Serializable;

/**
 * Tipos de alerta climática que puede registrar el administrador.
 * Cada tipo afecta a diferentes tipos de atracciones.
 */
public enum TipoAlerta implements Serializable {
    /** Tormenta eléctrica: cierra atracciones acuáticas y mecánicas de altura. */
    TORMENTA_ELECTRICA,
    /** Lluvia fuerte: cierra atracciones acuáticas y mecánicas de altura. */
    LLUVIA_FUERTE
}
