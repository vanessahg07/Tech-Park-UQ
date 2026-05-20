package co.edu.uq.techpark.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Registro de una visita completada a una atracción.
 * Se agrega al historial del visitante cada vez que completa un ciclo en una atracción.
 */
public class RegistroDeVisita implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador de la atracción visitada. */
    private String idAtraccion;

    /** Nombre de la atracción visitada (guardado para mostrarlo aunque la atracción cambie). */
    private String nombreAtraccion;

    /** Fecha y hora exacta en que el visitante completó la visita. */
    private LocalDateTime visitadaEn;

    public RegistroDeVisita(String idAtraccion, String nombreAtraccion, LocalDateTime visitadaEn) {
        this.idAtraccion = idAtraccion;
        this.nombreAtraccion = nombreAtraccion;
        this.visitadaEn = visitadaEn;
    }

    public String getIdAtraccion() { return idAtraccion; }
    public String getNombreAtraccion() { return nombreAtraccion; }
    public LocalDateTime getVisitadaEn() { return visitadaEn; }
}
