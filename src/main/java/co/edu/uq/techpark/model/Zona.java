package co.edu.uq.techpark.model;

import co.edu.uq.techpark.ds.ListaEnlazada;

import java.io.Serializable;
import java.util.UUID;

/**
 * Zona física del parque que agrupa varias atracciones.
 * Cada zona tiene un aforo máximo y una lista de operadores asignados.
 *
 * Cumple con el Requisito 9.3
 */
public class Zona implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único de la zona. */
    private String id;

    /** Nombre visible de la zona (ej: "Zona Aventura Extrema"). */
    private String nombre;

    /** Máximo de visitantes permitidos simultáneamente en esta zona. */
    private int aforoMaximo;

    /** Cantidad actual de visitantes en la zona. */
    private int visitantesActuales;

    /** Lista de operadores asignados a esta zona. */
    private ListaEnlazada<Personal> operadores;

    /** Lista de IDs de las atracciones que pertenecen a esta zona. */
    private ListaEnlazada<String> idsDeAtracciones;

    public Zona() {
        this.id = UUID.randomUUID().toString();
        this.operadores = new ListaEnlazada<>();
        this.idsDeAtracciones = new ListaEnlazada<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAforoMaximo() { return aforoMaximo; }
    public void setAforoMaximo(int aforoMaximo) { this.aforoMaximo = aforoMaximo; }

    public int getVisitantesActuales() { return visitantesActuales; }
    public void setVisitantesActuales(int visitantesActuales) { this.visitantesActuales = visitantesActuales; }

    public ListaEnlazada<Personal> getOperadores() { return operadores; }
    public void setOperadores(ListaEnlazada<Personal> operadores) { this.operadores = operadores; }

    public ListaEnlazada<String> getIdsDeAtracciones() { return idsDeAtracciones; }
    public void setIdsDeAtracciones(ListaEnlazada<String> idsDeAtracciones) { this.idsDeAtracciones = idsDeAtracciones; }
}
