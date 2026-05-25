package co.edu.uq.techpark.model;

import co.edu.uq.techpark.ds.ColaDePrioridad;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Atracción del parque de diversiones.
 * Cada atracción tiene su propia cola virtual de prioridad
 * donde los visitantes esperan su turno.
 *
 * Cumple con los Requisitos 4.1, 5.1
 */
public class Atraccion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único de la atracción. */
    private String id;

    /** Nombre visible de la atracción (ej: "Montaña Rusa Extrema"). */
    private String nombre;

    /** Categoría de la atracción: acuática, mecánica, espectáculo, etc. */
    private TipoAtraccion tipo;

    /** Máximo de visitantes que pueden montar en un solo ciclo. */
    private int capacidadMaximaPorCiclo;

    /** Estatura mínima en centímetros para poder subir. 0 = sin restricción. */
    private int estaturaMinimaEnCm;

    /** Edad mínima en años para poder subir. 0 = sin restricción. */
    private int edadMinima;

    /** Total de visitantes que han completado un ciclo en esta atracción durante la jornada. */
    private int visitantesAcumulados;

    /** Tiempo estimado de espera en minutos para el próximo ciclo. */
    private int minutosEsperaEstimados;

    /** Estado actual: ACTIVA, EN_MANTENIMIENTO o CERRADA. */
    private EstadoAtraccion estado;

    /** Identificador de la zona del parque a la que pertenece esta atracción. */
    private String idZona;

    /** Razón por la que se cerró la atracción. Null si está activa. */
    private String motivoCierre;

    /** Fecha y hora en que se cerró la atracción. Null si está activa. */
    private LocalDateTime fechaCierre;

    /** Cola virtual donde los visitantes esperan su turno para subir. */
    private ColaDePrioridad<Visitante> colaVirtual;

    public Atraccion() {
        this.id = UUID.randomUUID().toString();
        this.estado = EstadoAtraccion.ACTIVA;
        this.colaVirtual = new ColaDePrioridad<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoAtraccion getTipo() { return tipo; }
    public void setTipo(TipoAtraccion tipo) { this.tipo = tipo; }

    public int getCapacidadMaximaPorCiclo() { return capacidadMaximaPorCiclo; }
    public void setCapacidadMaximaPorCiclo(int capacidadMaximaPorCiclo) { this.capacidadMaximaPorCiclo = capacidadMaximaPorCiclo; }

    public int getEstaturaMinimaEnCm() { return estaturaMinimaEnCm; }
    public void setEstaturaMinimaEnCm(int estaturaMinimaEnCm) { this.estaturaMinimaEnCm = estaturaMinimaEnCm; }

    public int getEdadMinima() { return edadMinima; }
    public void setEdadMinima(int edadMinima) { this.edadMinima = edadMinima; }

    public int getVisitantesAcumulados() { return visitantesAcumulados; }
    public void setVisitantesAcumulados(int visitantesAcumulados) { this.visitantesAcumulados = visitantesAcumulados; }

    public int getMinutosEsperaEstimados() { return minutosEsperaEstimados; }
    public void setMinutosEsperaEstimados(int minutosEsperaEstimados) { this.minutosEsperaEstimados = minutosEsperaEstimados; }

    public EstadoAtraccion getEstado() { return estado; }
    public void setEstado(EstadoAtraccion estado) { this.estado = estado; }

    public String getIdZona() { return idZona; }
    public void setIdZona(String idZona) { this.idZona = idZona; }

    public String getMotivoCierre() { return motivoCierre; }
    public void setMotivoCierre(String motivoCierre) { this.motivoCierre = motivoCierre; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public ColaDePrioridad<Visitante> getColaVirtual() { return colaVirtual; }
    public void setColaVirtual(ColaDePrioridad<Visitante> colaVirtual) { this.colaVirtual = colaVirtual; }
}
