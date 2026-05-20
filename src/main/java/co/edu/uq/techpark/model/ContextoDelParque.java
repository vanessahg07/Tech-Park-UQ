package co.edu.uq.techpark.model;

import co.edu.uq.techpark.ds.ArbolBinarioBusqueda;
import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.ds.GrafoDelParque;

import javax.servlet.ServletContext;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Contenedor central de todos los datos del parque durante una jornada.
 * Se guarda en el ServletContext para que todos los servlets puedan acceder a él.
 * Es el único punto de verdad del estado del sistema.
 *
 * Cumple con los Requisitos 4.2, 6.1
 */
public class ContextoDelParque implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Clave usada para guardar y recuperar el contexto del ServletContext. */
    public static final String CLAVE_CONTEXTO = "parkContext";

    // ------------------------------------------------------------------ Estructuras de datos principales

    /** Árbol de búsqueda que indexa las atracciones por nombre para búsqueda rápida. */
    private ArbolBinarioBusqueda<String, Atraccion> atraccionesPorNombre;

    /** Árbol de búsqueda que indexa las atracciones por ID para búsqueda rápida. */
    private ArbolBinarioBusqueda<String, Atraccion> atraccionesPorId;

    /** Grafo del parque: nodos = atracciones, aristas = caminos con distancia en metros. */
    private GrafoDelParque<String> grafoDelParque;

    /** Árbol de búsqueda de visitantes, indexados por número de documento. */
    private ArbolBinarioBusqueda<String, Visitante> visitantes;

    /** Árbol de búsqueda del personal, indexado por nombre de usuario. */
    private ArbolBinarioBusqueda<String, Personal> personal;

    /** Árbol de búsqueda de zonas, indexadas por ID de zona. */
    private ArbolBinarioBusqueda<String, Zona> zonas;

    /** Lista de alertas climáticas actualmente activas. */
    private ListaEnlazada<AlertaClimatica> alertasActivas;

    /** Lista de todas las alertas climáticas que han ocurrido en la jornada. */
    private ListaEnlazada<AlertaClimatica> historialDeAlertas;

    // ------------------------------------------------------------------ Configuración del parque

    /** Máximo de visitantes permitidos simultáneamente en todo el parque. */
    private int aforoMaximoDelParque;

    /** Cantidad actual de visitantes con tiquete activo en el parque. */
    private int visitantesActualesEnElParque;

    /** Fecha de la jornada actual. */
    private LocalDate fechaJornadaActual;

    // ------------------------------------------------------------------ Constructor

    public ContextoDelParque() {
        this.atraccionesPorNombre = new ArbolBinarioBusqueda<>();
        this.atraccionesPorId     = new ArbolBinarioBusqueda<>();
        this.grafoDelParque       = new GrafoDelParque<>();
        this.visitantes           = new ArbolBinarioBusqueda<>();
        this.personal             = new ArbolBinarioBusqueda<>();
        this.zonas                = new ArbolBinarioBusqueda<>();
        this.alertasActivas       = new ListaEnlazada<>();
        this.historialDeAlertas   = new ListaEnlazada<>();
        this.aforoMaximoDelParque = 500;
        this.visitantesActualesEnElParque = 0;
        this.fechaJornadaActual   = LocalDate.now();
    }

    // ------------------------------------------------------------------ Métodos estáticos de acceso

    /**
     * Retorna el contexto guardado en el ServletContext.
     * Retorna null si todavía no se ha inicializado.
     */
    public static ContextoDelParque obtenerInstancia(ServletContext ctx) {
        return (ContextoDelParque) ctx.getAttribute(CLAVE_CONTEXTO);
    }

    /**
     * Crea un nuevo contexto vacío, lo guarda en el ServletContext y lo retorna.
     * Se llama al iniciar la aplicación.
     */
    public static ContextoDelParque inicializar(ServletContext ctx) {
        ContextoDelParque contexto = new ContextoDelParque();
        ctx.setAttribute(CLAVE_CONTEXTO, contexto);
        return contexto;
    }

    // ------------------------------------------------------------------ Getters y Setters

    public ArbolBinarioBusqueda<String, Atraccion> getAtraccionesPorNombre() { return atraccionesPorNombre; }
    public void setAtraccionesPorNombre(ArbolBinarioBusqueda<String, Atraccion> v) { this.atraccionesPorNombre = v; }

    public ArbolBinarioBusqueda<String, Atraccion> getAtraccionesPorId() { return atraccionesPorId; }
    public void setAtraccionesPorId(ArbolBinarioBusqueda<String, Atraccion> v) { this.atraccionesPorId = v; }

    public GrafoDelParque<String> getGrafoDelParque() { return grafoDelParque; }
    public void setGrafoDelParque(GrafoDelParque<String> v) { this.grafoDelParque = v; }

    public ArbolBinarioBusqueda<String, Visitante> getVisitantes() { return visitantes; }
    public void setVisitantes(ArbolBinarioBusqueda<String, Visitante> v) { this.visitantes = v; }

    public ArbolBinarioBusqueda<String, Personal> getPersonal() { return personal; }
    public void setPersonal(ArbolBinarioBusqueda<String, Personal> v) { this.personal = v; }

    public ArbolBinarioBusqueda<String, Zona> getZonas() { return zonas; }
    public void setZonas(ArbolBinarioBusqueda<String, Zona> v) { this.zonas = v; }

    public ListaEnlazada<AlertaClimatica> getAlertasActivas() { return alertasActivas; }
    public void setAlertasActivas(ListaEnlazada<AlertaClimatica> v) { this.alertasActivas = v; }

    public ListaEnlazada<AlertaClimatica> getHistorialDeAlertas() { return historialDeAlertas; }
    public void setHistorialDeAlertas(ListaEnlazada<AlertaClimatica> v) { this.historialDeAlertas = v; }

    public int getAforoMaximoDelParque() { return aforoMaximoDelParque; }
    public void setAforoMaximoDelParque(int v) { this.aforoMaximoDelParque = v; }

    public int getVisitantesActualesEnElParque() { return visitantesActualesEnElParque; }
    public void setVisitantesActualesEnElParque(int v) { this.visitantesActualesEnElParque = v; }

    public LocalDate getFechaJornadaActual() { return fechaJornadaActual; }
    public void setFechaJornadaActual(LocalDate v) { this.fechaJornadaActual = v; }
}
