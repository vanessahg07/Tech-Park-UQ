package co.edu.uq.techpark.service;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.*;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import java.time.LocalDateTime;

/**
 * Servicio para gestionar alertas climáticas y su efecto sobre las atracciones.
 * Requisitos: 8.1, 8.2, 8.3, 8.4
 */
public class ServicioDeAlertas {

    private ServicioDeAlertas() {}

    public static AlertaClimatica registrarAlerta(TipoAlerta tipo, ContextoDelParque contexto) {
        LocalDateTime ahora = LocalDateTime.now();
        AlertaClimatica alerta = new AlertaClimatica();
        alerta.setTipo(tipo);
        alerta.setHoraInicio(ahora);

        ListaEnlazada<Atraccion> atracciones = contexto.getAtraccionesPorId().enOrden();
        for (ListaEnlazada.Iterador<Atraccion> it = atracciones.iterador(); it.tieneSiguiente(); ) {
            Atraccion atraccion = it.siguiente();
            TipoAtraccion tipoAtraccion = atraccion.getTipo();
            // Solo se cierran atracciones acuáticas y mecánicas de altura que estén activas
            if ((tipoAtraccion == TipoAtraccion.ACUATICA || tipoAtraccion == TipoAtraccion.MECANICA_DE_ALTURA)
                    && atraccion.getEstado() == EstadoAtraccion.ACTIVA) {
                atraccion.setEstado(EstadoAtraccion.CERRADA);
                atraccion.setMotivoCierre("clima");
                atraccion.setFechaCierre(ahora);
                alerta.getIdsDeAtraccionesAfectadas().agregarAlFinal(atraccion.getId());
            }
        }

        contexto.getAlertasActivas().agregarAlFinal(alerta);
        return alerta;
    }

    public static void cancelarAlerta(String idAlerta, ContextoDelParque contexto) throws ExcepcionDelParque {
        AlertaClimatica encontrada = null;
        for (ListaEnlazada.Iterador<AlertaClimatica> it = contexto.getAlertasActivas().iterador(); it.tieneSiguiente(); ) {
            AlertaClimatica alerta = it.siguiente();
            if (alerta.getId().equals(idAlerta)) {
                encontrada = alerta;
                break;
            }
        }
        if (encontrada == null) throw new ExcepcionDelParque("Alerta no encontrada: " + idAlerta);
        encontrada.setHoraFin(LocalDateTime.now());
        contexto.getAlertasActivas().eliminar(encontrada);
        contexto.getHistorialDeAlertas().agregarAlFinal(encontrada);
    }
}
