package co.edu.uq.techpark.service;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.EstadoAtraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.model.RegistroDeVisita;
import co.edu.uq.techpark.util.ExcepcionDelParque;
import java.time.LocalDateTime;

/**
 * Servicio para gestionar las operaciones de la cola virtual de las atracciones.
 * Requisitos: 3.1, 3.2, 3.3, 3.4, 4.6, 5.1, 5.2, 5.3, 5.4, 5.5
 */
public class ServicioDeCola {

    private ServicioDeCola() {}

    public static void encolar(Visitante visitante, Atraccion atraccion, ContextoDelParque contexto)
            throws ExcepcionDelParque {
        if (atraccion.getEstado() != EstadoAtraccion.ACTIVA)
            throw new ExcepcionDelParque("Atracción no disponible");
        if (visitante.getTiqueteActivo() == null)
            throw new ExcepcionDelParque("El visitante no tiene tiquete activo");
        if (visitante.getEstaturaCm() < atraccion.getEstaturaMinimaEnCm())
            throw new ExcepcionDelParque("Estatura insuficiente: mínimo " + atraccion.getEstaturaMinimaEnCm() + " cm");
        if (visitante.getEdad() < atraccion.getEdadMinima())
            throw new ExcepcionDelParque("Edad insuficiente: mínimo " + atraccion.getEdadMinima() + " años");
        if (visitante.getSaldoVirtual() < atraccion.getCostoAdicional())
            throw new ExcepcionDelParque("Saldo insuficiente: requerido " + atraccion.getCostoAdicional()
                    + ", disponible " + visitante.getSaldoVirtual());
        atraccion.getColaVirtual().encolar(visitante, visitante.getTiqueteActivo().getPrioridad());
    }

    /** Procesa un ciclo: desencola hasta capacidadMaximaPorCiclo visitantes. Los retorna como ListaEnlazada. */
    public static ListaEnlazada<Visitante> desencolar(Atraccion atraccion, ContextoDelParque contexto) {
        ListaEnlazada<Visitante> procesados = new ListaEnlazada<>();
        for (int i = 0; i < atraccion.getCapacidadMaximaPorCiclo(); i++) {
            Visitante visitante = atraccion.getColaVirtual().desencolar();
            if (visitante == null) break;
            visitante.setSaldoVirtual(visitante.getSaldoVirtual() - atraccion.getCostoAdicional());
            visitante.getHistorialDeVisitas().agregarAlFinal(
                    new RegistroDeVisita(atraccion.getId(), atraccion.getNombre(), LocalDateTime.now()));
            atraccion.setVisitantesAcumulados(atraccion.getVisitantesAcumulados() + 1);
            procesados.agregarAlFinal(visitante);

            // Notificar al visitante que acaba de ser procesado en el ciclo
            ServicioDeNotificaciones.notificar(visitante,
                    "🎢 ¡Disfrutaste \"" + atraccion.getNombre() + "\"! Se descontaron $"
                    + String.format("%,.2f", atraccion.getCostoAdicional()) + " de tu saldo.");
        }

        ServicioDeAtracciones.verificarMantenimientoPreventivo(atraccion, contexto);

        // Notificar al siguiente visitante en la cola que es su turno
        Visitante siguiente = atraccion.getColaVirtual().verPrimero();
        if (siguiente != null && atraccion.getEstado() == EstadoAtraccion.ACTIVA) {
            ServicioDeNotificaciones.notificar(siguiente,
                    "🔔 ¡Es tu turno! Eres el primero en la cola de \"" + atraccion.getNombre()
                    + "\". Dirígete a la atracción.");
        }

        return procesados;
    }

    public static boolean cancelar(Visitante visitante, Atraccion atraccion) {
        return atraccion.getColaVirtual().eliminar(visitante);
    }

    public static int obtenerPosicion(Visitante visitante, Atraccion atraccion) {
        return atraccion.getColaVirtual().posicionDe(visitante);
    }

    public static int calcularTiempoEspera(Visitante visitante, Atraccion atraccion) {
        int posicion = obtenerPosicion(visitante, atraccion);
        if (posicion == -1) return -1;
        int capacidad = atraccion.getCapacidadMaximaPorCiclo();
        if (capacidad <= 0) return -1;
        return (posicion / capacidad) * atraccion.getMinutosEsperaEstimados();
    }
}
