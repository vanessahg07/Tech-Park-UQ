package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.EstadoAtraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.util.ExcepcionDelParque;
import java.time.LocalDateTime;

/**
 * Servicio para gestionar las atracciones del parque.
 * Requisitos: 4.1, 4.2, 4.3, 4.4, 4.5
 */
public class ServicioDeAtracciones {

    private ServicioDeAtracciones() {}

    public static void agregar(Atraccion atraccion, ContextoDelParque contexto) {
        contexto.getAtraccionesPorId().insertar(atraccion.getId(), atraccion);
        contexto.getAtraccionesPorNombre().insertar(atraccion.getNombre(), atraccion);
        contexto.getGrafoDelParque().agregarNodo(atraccion.getId());
    }

    public static void actualizar(Atraccion atraccion, ContextoDelParque contexto) throws ExcepcionDelParque {
        Atraccion existente = contexto.getAtraccionesPorId().buscar(atraccion.getId());
        if (existente == null) throw new ExcepcionDelParque("Atracción no encontrada: " + atraccion.getId());
        if (!existente.getNombre().equals(atraccion.getNombre())) {
            contexto.getAtraccionesPorNombre().eliminar(existente.getNombre());
            contexto.getAtraccionesPorNombre().insertar(atraccion.getNombre(), existente);
        }
        existente.setNombre(atraccion.getNombre());
        existente.setTipo(atraccion.getTipo());
        existente.setCapacidadMaximaPorCiclo(atraccion.getCapacidadMaximaPorCiclo());
        existente.setEstaturaMinimaEnCm(atraccion.getEstaturaMinimaEnCm());
        existente.setEdadMinima(atraccion.getEdadMinima());
        existente.setCostoAdicional(atraccion.getCostoAdicional());
        existente.setIdZona(atraccion.getIdZona());
    }

    public static void cambiarEstado(String idAtraccion, EstadoAtraccion nuevoEstado,
                                     String motivo, ContextoDelParque contexto) throws ExcepcionDelParque {
        Atraccion atraccion = contexto.getAtraccionesPorId().buscar(idAtraccion);
        if (atraccion == null) throw new ExcepcionDelParque("Atracción no encontrada: " + idAtraccion);

        EstadoAtraccion estadoAnterior = atraccion.getEstado();
        atraccion.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoAtraccion.CERRADA || nuevoEstado == EstadoAtraccion.EN_MANTENIMIENTO) {
            atraccion.setMotivoCierre(motivo);
            atraccion.setFechaCierre(LocalDateTime.now());

            // Notificar a todos los visitantes que estaban en la cola de esta atracción
            String razon = nuevoEstado == EstadoAtraccion.EN_MANTENIMIENTO
                    ? "mantenimiento" : (motivo != null && !motivo.isEmpty() ? motivo : "cierre operativo");
            String emoji = nuevoEstado == EstadoAtraccion.EN_MANTENIMIENTO ? "🔧" : "🚫";
            notificarColaAtraccion(atraccion, contexto,
                    emoji + " La atracción \"" + atraccion.getNombre() + "\" ha sido cerrada por " + razon
                    + ". Tu turno en la cola ha sido cancelado.");
        } else {
            atraccion.setMotivoCierre(null);
            atraccion.setFechaCierre(null);
        }
    }

    public static Atraccion buscarPorId(String id, ContextoDelParque contexto) {
        return contexto.getAtraccionesPorId().buscar(id);
    }

    public static Atraccion buscarPorNombre(String nombre, ContextoDelParque contexto) {
        return contexto.getAtraccionesPorNombre().buscar(nombre);
    }

    /**
     * Verifica si la atracción debe entrar en mantenimiento preventivo
     * al superar los 500 visitantes acumulados.
     */
    public static void verificarMantenimientoPreventivo(Atraccion atraccion, ContextoDelParque contexto) {
        if (atraccion.getVisitantesAcumulados() >= 500
                && atraccion.getEstado() == EstadoAtraccion.ACTIVA) {
            atraccion.setEstado(EstadoAtraccion.EN_MANTENIMIENTO);
            atraccion.setMotivoCierre("mantenimiento preventivo");
            atraccion.setFechaCierre(LocalDateTime.now());

            // Notificar a los visitantes que aún quedaron en cola
            notificarColaAtraccion(atraccion, contexto,
                    "🔧 La atracción \"" + atraccion.getNombre() + "\" ha entrado en mantenimiento preventivo "
                    + "(500 visitantes alcanzados). Tu turno en la cola ha sido cancelado.");
        }
    }

    /**
     * Notifica a todos los visitantes que están actualmente en la cola de una atracción.
     * Se usa cuando la atracción cierra o entra en mantenimiento.
     */
    static void notificarColaAtraccion(Atraccion atraccion, ContextoDelParque contexto, String mensaje) {
        co.edu.uq.techpark.ds.ListaEnlazada<co.edu.uq.techpark.model.Visitante> enCola =
                atraccion.getColaVirtual().instantanea();
        for (co.edu.uq.techpark.ds.ListaEnlazada.Iterador<co.edu.uq.techpark.model.Visitante> it =
                enCola.iterador(); it.tieneSiguiente(); ) {
            ServicioDeNotificaciones.notificar(it.siguiente(), mensaje);
        }
    }
}
