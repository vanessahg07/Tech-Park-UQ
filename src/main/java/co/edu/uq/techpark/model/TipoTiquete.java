package co.edu.uq.techpark.model;

import java.io.Serializable;

/**
 * Tipos de tiquete que puede comprar un visitante.
 * El tipo determina el precio y la prioridad en la cola virtual.
 */
public enum TipoTiquete implements Serializable {
    /** Tiquete estándar. Prioridad 2 en la cola. */
    GENERAL,
    /** Tiquete con descuento familiar. Prioridad 2 en la cola. */
    FAMILIAR,
    /** Tiquete premium. Prioridad 1 en la cola (se atiende antes que los demás). */
    PASE_RAPIDO
}
