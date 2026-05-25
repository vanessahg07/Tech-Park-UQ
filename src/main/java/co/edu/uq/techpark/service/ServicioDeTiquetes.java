package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Tiquete;
import co.edu.uq.techpark.model.TipoTiquete;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para la compra de tiquetes de acceso al parque.
 * Requisitos: 2.1, 2.2, 2.3, 2.4, 2.5
 */
public class ServicioDeTiquetes {

    public static final double PRECIO_GENERAL     = 65000.0;
    public static final double DESCUENTO_FAMILIAR  = 0.15;
    public static final double PRECIO_PASE_RAPIDO  = 100000.0;

    private ServicioDeTiquetes() {}

    public static Tiquete comprar(Visitante visitante, TipoTiquete tipo, ContextoDelParque contexto) throws ExcepcionDelParque {
        if (contexto.getVisitantesActualesEnElParque() >= contexto.getAforoMaximoDelParque()) {
            throw new ExcepcionDelParque("Aforo del parque completo");
        }

        double precio;
        int prioridad;

        switch (tipo) {
            case GENERAL:     precio = PRECIO_GENERAL; prioridad = 2; break;
            case FAMILIAR:    precio = PRECIO_GENERAL * (1 - DESCUENTO_FAMILIAR); prioridad = 2; break;
            case PASE_RAPIDO: precio = PRECIO_PASE_RAPIDO; prioridad = 1; break;
            default: throw new ExcepcionDelParque("Tipo de tiquete no reconocido");
        }

        if (visitante.getSaldoVirtual() < precio) {
            throw new ExcepcionDelParque("Saldo virtual insuficiente para comprar este tiquete");
        }

        Tiquete tiquete = new Tiquete(UUID.randomUUID().toString(), tipo, precio, prioridad, true, LocalDateTime.now());
        visitante.setTiqueteActivo(tiquete);
        visitante.setSaldoVirtual(visitante.getSaldoVirtual() - precio);
        contexto.setVisitantesActualesEnElParque(contexto.getVisitantesActualesEnElParque() + 1);
        contexto.agregarIngresoPorTiquete(precio);
        return tiquete;
    }
}
