package co.edu.uq.techpark.model;

import java.io.Serializable;

/**
 * Estado operativo de una atracción del parque.
 * Determina si los visitantes pueden unirse a su cola virtual.
 */
public enum EstadoAtraccion implements Serializable {
    /** La atracción está funcionando y acepta visitantes. */
    ACTIVA,
    /** La atracción está en mantenimiento preventivo (alcanzó 500 visitantes). */
    EN_MANTENIMIENTO,
    /** La atracción está cerrada por clima u otro motivo. */
    CERRADA
}
