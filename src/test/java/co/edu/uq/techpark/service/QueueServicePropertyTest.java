package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.*;
import co.edu.uq.techpark.util.ExcepcionDelParque;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 9:  Restricciones de seguridad siempre verificadas — Validates: Requirements 3.1-3.4
 * Property 10: Atracción no activa rechaza enqueue — Validates: Requirements 4.6
 * Property 14: Saldo insuficiente siempre deniega el acceso — Validates: Requirements 1.5
 */
class QueueServicePropertyTest {

    private static Tiquete tiqueteActivo() {
        return new Tiquete(UUID.randomUUID().toString(), TipoTiquete.GENERAL, 10.0, 2, true, LocalDateTime.now());
    }

    private static Visitante crearVisitante(int edad, int estaturaCm, double saldo) {
        Visitante v = new Visitante();
        v.setNumeroDocumento(UUID.randomUUID().toString());
        v.setEdad(edad);
        v.setEstaturaCm(estaturaCm);
        v.setSaldoVirtual(saldo);
        v.setTiqueteActivo(tiqueteActivo());
        return v;
    }

    private static Atraccion crearAtraccion(int edadMin, int estaturaMinCm, double costo, EstadoAtraccion estado) {
        Atraccion a = new Atraccion();
        a.setNombre("A-" + UUID.randomUUID());
        a.setEdadMinima(edadMin);
        a.setEstaturaMinimaEnCm(estaturaMinCm);
        a.setCostoAdicional(costo);
        a.setEstado(estado);
        a.setCapacidadMaximaPorCiclo(10);
        a.setMinutosEsperaEstimados(5);
        return a;
    }

    // P9 — restricción de estatura
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 9: Restricciones de seguridad siempre verificadas")
    void visitanteBajoEstaturaMinima_esRechazado(
            @ForAll @IntRange(min = 51, max = 250) int estaturaMin,
            @ForAll @IntRange(min = 1, max = 50) int deficit) {

        Visitante visitante = crearVisitante(18, estaturaMin - deficit, 1000.0);
        Atraccion atraccion = crearAtraccion(0, estaturaMin, 0.0, EstadoAtraccion.ACTIVA);
        int antes = atraccion.getColaVirtual().tamanio();
        assertThrows(ExcepcionDelParque.class,
                () -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
        assertEquals(antes, atraccion.getColaVirtual().tamanio());
    }

    // P9 — restricción de edad
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 9: Restricciones de seguridad siempre verificadas")
    void visitanteBajoEdadMinima_esRechazado(
            @ForAll @IntRange(min = 2, max = 100) int edadMin) {

        Visitante visitante = crearVisitante(edadMin - 1, 200, 1000.0);
        Atraccion atraccion = crearAtraccion(edadMin, 0, 0.0, EstadoAtraccion.ACTIVA);
        int antes = atraccion.getColaVirtual().tamanio();
        assertThrows(ExcepcionDelParque.class,
                () -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
        assertEquals(antes, atraccion.getColaVirtual().tamanio());
    }

    // P9 — visitante que cumple restricciones es admitido
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 9: Restricciones de seguridad siempre verificadas")
    void visitanteCumpleRestricciones_esAdmitido(
            @ForAll @IntRange(min = 1, max = 80) int edadMin,
            @ForAll @IntRange(min = 50, max = 200) int estaturaMin) {

        Visitante visitante = crearVisitante(edadMin, estaturaMin, 1000.0);
        Atraccion atraccion = crearAtraccion(edadMin, estaturaMin, 0.0, EstadoAtraccion.ACTIVA);
        assertDoesNotThrow(() -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
        assertTrue(atraccion.getColaVirtual().posicionDe(visitante) >= 1);
    }

    // P10 — EN_MANTENIMIENTO rechaza
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 10: Atraccion no activa rechaza enqueue")
    void atraccionEnMantenimiento_rechazaEncolar(@ForAll @IntRange(min = 18, max = 80) int edad) {
        Visitante visitante = crearVisitante(edad, 150, 1000.0);
        Atraccion atraccion = crearAtraccion(0, 0, 0.0, EstadoAtraccion.EN_MANTENIMIENTO);
        int antes = atraccion.getColaVirtual().tamanio();
        assertThrows(ExcepcionDelParque.class,
                () -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
        assertEquals(antes, atraccion.getColaVirtual().tamanio());
    }

    // P10 — CERRADA rechaza
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 10: Atraccion no activa rechaza enqueue")
    void atraccionCerrada_rechazaEncolar(@ForAll @IntRange(min = 18, max = 80) int edad) {
        Visitante visitante = crearVisitante(edad, 150, 1000.0);
        Atraccion atraccion = crearAtraccion(0, 0, 0.0, EstadoAtraccion.CERRADA);
        int antes = atraccion.getColaVirtual().tamanio();
        assertThrows(ExcepcionDelParque.class,
                () -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
        assertEquals(antes, atraccion.getColaVirtual().tamanio());
    }

    // P14 — saldo insuficiente
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 14: Saldo insuficiente siempre deniega el acceso")
    void saldoInsuficiente_denegaAcceso(
            @ForAll @IntRange(min = 1, max = 10_000) int costo) {

        double saldo = costo - 1;
        Visitante visitante = crearVisitante(18, 150, saldo);
        Atraccion atraccion = crearAtraccion(0, 0, (double) costo, EstadoAtraccion.ACTIVA);
        double antes = visitante.getSaldoVirtual();
        assertThrows(ExcepcionDelParque.class,
                () -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
        assertEquals(antes, visitante.getSaldoVirtual(), 1e-9);
    }

    // P14 — saldo exacto es admitido
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 14: Saldo insuficiente siempre deniega el acceso")
    void saldoExacto_esAdmitido(@ForAll @IntRange(min = 0, max = 10_000) int costo) {
        Visitante visitante = crearVisitante(18, 150, (double) costo);
        Atraccion atraccion = crearAtraccion(0, 0, (double) costo, EstadoAtraccion.ACTIVA);
        assertDoesNotThrow(() -> ServicioDeCola.encolar(visitante, atraccion, new ContextoDelParque()));
    }
}
