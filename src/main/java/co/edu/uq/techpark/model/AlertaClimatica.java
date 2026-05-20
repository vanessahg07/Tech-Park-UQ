package co.edu.uq.techpark.model;

import co.edu.uq.techpark.ds.ListaEnlazada;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Alerta climática registrada por el administrador.
 * Al registrarse, cierra automáticamente las atracciones acuáticas
 * y mecánicas de altura para proteger a los visitantes.
 *
 * Cumple con el Requisito 8.4
 */
public class AlertaClimatica implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único de la alerta. */
    private String id;

    /** Tipo de alerta: tormenta eléctrica o lluvia fuerte. */
    private TipoAlerta tipo;

    /** Fecha y hora en que se registró la alerta. */
    private LocalDateTime horaInicio;

    /** Fecha y hora en que se canceló la alerta. Null si sigue activa. */
    private LocalDateTime horaFin;

    /** IDs de las atracciones que fueron cerradas por esta alerta. */
    private ListaEnlazada<String> idsDeAtraccionesAfectadas;

    public AlertaClimatica() {
        this.id = UUID.randomUUID().toString();
        this.idsDeAtraccionesAfectadas = new ListaEnlazada<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }

    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalDateTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalDateTime horaFin) { this.horaFin = horaFin; }

    public ListaEnlazada<String> getIdsDeAtraccionesAfectadas() { return idsDeAtraccionesAfectadas; }
    public void setIdsDeAtraccionesAfectadas(ListaEnlazada<String> ids) { this.idsDeAtraccionesAfectadas = ids; }
}
