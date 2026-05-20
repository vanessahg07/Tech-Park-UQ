package co.edu.uq.techpark.model;

import java.io.Serializable;

/**
 * Categoría de una atracción del parque.
 * Determina qué alertas climáticas la afectan.
 */
public enum TipoAtraccion implements Serializable {
    /** Atracción con agua: toboganes, ríos, etc. Se cierra con alertas climáticas. */
    ACUATICA,
    /** Atracción mecánica en altura: montañas rusas, torres. Se cierra con alertas climáticas. */
    MECANICA_DE_ALTURA,
    /** Atracción mecánica sin altura significativa: carruseles, trenes. */
    MECANICA,
    /** Espectáculo o show: teatro, circo, etc. */
    ESPECTACULO,
    /** Otro tipo de atracción no clasificada. */
    OTRA
}
