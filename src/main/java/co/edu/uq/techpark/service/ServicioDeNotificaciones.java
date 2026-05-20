package co.edu.uq.techpark.service;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Notificacion;
import co.edu.uq.techpark.model.Visitante;

import java.time.LocalDateTime;

/**
 * Servicio para gestionar las notificaciones de los visitantes.
 * Requisitos: 10.1, 10.2, 10.4
 */
public class ServicioDeNotificaciones {

    private ServicioDeNotificaciones() {}

    public static void notificar(Visitante visitante, String mensaje) {
        visitante.getNotificacionesSinLeer().agregarAlFinal(new Notificacion(mensaje, LocalDateTime.now()));
    }

    public static boolean marcarComoLeida(Visitante visitante, String idNotificacion) {
        ListaEnlazada<Notificacion> notificaciones = visitante.getNotificacionesSinLeer();
        for (ListaEnlazada.Iterador<Notificacion> it = notificaciones.iterador(); it.tieneSiguiente(); ) {
            Notificacion n = it.siguiente();
            if (n.getId().equals(idNotificacion)) {
                n.setLeida(true);
                notificaciones.eliminar(n);
                return true;
            }
        }
        return false;
    }
}
