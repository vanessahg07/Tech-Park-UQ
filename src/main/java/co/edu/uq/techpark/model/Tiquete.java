package co.edu.uq.techpark.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tiquete digital que autoriza al visitante a ingresar al parque
 * y a unirse a las colas virtuales de las atracciones.
 */
public class Tiquete implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único del tiquete. */
    private String id;

    /** Categoría del tiquete: GENERAL, FAMILIAR o PASE_RAPIDO. */
    private TipoTiquete tipo;

    /** Precio pagado por el tiquete en pesos. */
    private double precio;

    /**
     * Nivel de prioridad en la cola virtual.
     * 1 = Pase Rápido (se atiende primero), 2 = General o Familiar.
     */
    private int prioridad;

    /** Indica si el tiquete está vigente para la jornada actual. */
    private boolean activo;

    /** Fecha y hora en que se compró el tiquete. */
    private LocalDateTime compradoEn;

    public Tiquete(String id, TipoTiquete tipo, double precio, int prioridad,
                   boolean activo, LocalDateTime compradoEn) {
        this.id = id;
        this.tipo = tipo;
        this.precio = precio;
        this.prioridad = prioridad;
        this.activo = activo;
        this.compradoEn = compradoEn;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoTiquete getTipo() { return tipo; }
    public void setTipo(TipoTiquete tipo) { this.tipo = tipo; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCompradoEn() { return compradoEn; }
    public void setCompradoEn(LocalDateTime compradoEn) { this.compradoEn = compradoEn; }
}
