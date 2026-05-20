package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.EstadoAtraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 8: Mantenimiento preventivo al alcanzar 500 visitantes
 * Validates: Requirements 4.5
 */
class AttractionServicePropertyTest {

    private static Atraccion crearAtraccion(int visitantesAcumulados, EstadoAtraccion estado) {
        Atraccion a = new Atraccion();
        a.setNombre("Atraccion-" + visitantesAcumulados);
        a.setVisitantesAcumulados(visitantesAcumulados);
        a.setEstado(estado);
        return a;
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 8: Mantenimiento preventivo al alcanzar 500 visitantes")
    void atraccionActivaConSuficientesVisitantes_pasaAMantenimiento(
            @ForAll @IntRange(min = 500, max = 10_000) int visitantes) {

        ContextoDelParque contexto = new ContextoDelParque();
        Atraccion atraccion = crearAtraccion(visitantes, EstadoAtraccion.ACTIVA);
        ServicioDeAtracciones.agregar(atraccion, contexto);
        ServicioDeAtracciones.verificarMantenimientoPreventivo(atraccion, contexto);

        assertEquals(EstadoAtraccion.EN_MANTENIMIENTO, atraccion.getEstado());
        assertNotNull(atraccion.getMotivoCierre());
        assertNotNull(atraccion.getFechaCierre());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 8: Mantenimiento preventivo al alcanzar 500 visitantes")
    void atraccionActivaBajoUmbral_permaneceActiva(
            @ForAll @IntRange(min = 0, max = 499) int visitantes) {

        ContextoDelParque contexto = new ContextoDelParque();
        Atraccion atraccion = crearAtraccion(visitantes, EstadoAtraccion.ACTIVA);
        ServicioDeAtracciones.agregar(atraccion, contexto);
        ServicioDeAtracciones.verificarMantenimientoPreventivo(atraccion, contexto);

        assertEquals(EstadoAtraccion.ACTIVA, atraccion.getEstado());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 8: Mantenimiento preventivo al alcanzar 500 visitantes")
    void atraccionEnMantenimiento_noAfectadaPorDisparo(
            @ForAll @IntRange(min = 0, max = 10_000) int visitantes) {

        ContextoDelParque contexto = new ContextoDelParque();
        Atraccion atraccion = crearAtraccion(visitantes, EstadoAtraccion.EN_MANTENIMIENTO);
        ServicioDeAtracciones.agregar(atraccion, contexto);
        ServicioDeAtracciones.verificarMantenimientoPreventivo(atraccion, contexto);

        assertEquals(EstadoAtraccion.EN_MANTENIMIENTO, atraccion.getEstado());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 8: Mantenimiento preventivo al alcanzar 500 visitantes")
    void atraccionCerrada_noAfectadaPorDisparo(
            @ForAll @IntRange(min = 0, max = 10_000) int visitantes) {

        ContextoDelParque contexto = new ContextoDelParque();
        Atraccion atraccion = crearAtraccion(visitantes, EstadoAtraccion.CERRADA);
        ServicioDeAtracciones.agregar(atraccion, contexto);
        ServicioDeAtracciones.verificarMantenimientoPreventivo(atraccion, contexto);

        assertEquals(EstadoAtraccion.CERRADA, atraccion.getEstado());
    }
}
