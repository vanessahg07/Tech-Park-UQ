package co.edu.uq.techpark.ds;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.UniqueElements;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 2: Prioridad Fast-Pass siempre primero — Validates: Requirements 2.3, 5.1
 * Property 3: FIFO dentro del mismo nivel de prioridad — Validates: Requirements 2.4, 5.2
 * Property 11: Cancelación de posición reduce la cola en exactamente uno — Validates: Requirements 5.5
 */
class PriorityQueuePropertyTest {

    // P2 — Fast-Pass siempre antes que General
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 2: Prioridad Fast-Pass siempre primero")
    void elementosFastPassSiempreDesencolanAntesDeLosGenerales(
            @ForAll @Size(min = 1, max = 20) List<String> fastPass,
            @ForAll @Size(min = 1, max = 20) List<String> generales) {

        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        int fp = 0, gen = 0;
        while (fp < fastPass.size() || gen < generales.size()) {
            if (fp < fastPass.size()) cola.encolar(fastPass.get(fp++), 1);
            if (gen < generales.size()) cola.encolar(generales.get(gen++), 2);
        }

        for (int i = 0; i < fastPass.size(); i++) {
            String desencolado = cola.desencolar();
            assertNotNull(desencolado);
            assertTrue(fastPass.contains(desencolado),
                    "Se esperaba un elemento Fast-Pass pero se obtuvo: " + desencolado);
        }
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 2: Prioridad Fast-Pass siempre primero")
    void verPrimeroRetornaFastPassCuandoHayPresentes(
            @ForAll @Size(min = 1, max = 10) List<String> fastPass,
            @ForAll @Size(min = 1, max = 10) List<String> generales) {

        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        for (String s : generales) cola.encolar(s, 2);
        for (String s : fastPass)  cola.encolar(s, 1);

        for (int i = 0; i < fastPass.size(); i++) {
            String primero = cola.verPrimero();
            assertTrue(fastPass.contains(primero),
                    "verPrimero() debe retornar Fast-Pass pero retornó: " + primero);
            cola.desencolar();
        }
    }

    // P3 — FIFO dentro del mismo nivel
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 3: FIFO dentro del mismo nivel de prioridad")
    void fifoPreservadoEnPrioridad1(@ForAll @Size(min = 1, max = 30) List<String> items) {
        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        for (String s : items) cola.encolar(s, 1);
        List<String> resultado = new ArrayList<>();
        while (!cola.estaVacia()) resultado.add(cola.desencolar());
        assertEquals(items, resultado, "Los elementos de prioridad 1 deben salir en orden FIFO");
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 3: FIFO dentro del mismo nivel de prioridad")
    void fifoPreservadoEnPrioridad2(@ForAll @Size(min = 1, max = 30) List<String> items) {
        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        for (String s : items) cola.encolar(s, 2);
        List<String> resultado = new ArrayList<>();
        while (!cola.estaVacia()) resultado.add(cola.desencolar());
        assertEquals(items, resultado, "Los elementos de prioridad 2 deben salir en orden FIFO");
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 3: FIFO dentro del mismo nivel de prioridad")
    void fifoGeneralTrasAgotarFastPass(
            @ForAll @Size(min = 1, max = 10) List<String> fastPass,
            @ForAll @Size(min = 1, max = 10) List<String> generales) {

        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        for (String s : fastPass)  cola.encolar(s, 1);
        for (String s : generales) cola.encolar(s, 2);
        for (int i = 0; i < fastPass.size(); i++) cola.desencolar();

        List<String> resultado = new ArrayList<>();
        while (!cola.estaVacia()) resultado.add(cola.desencolar());
        assertEquals(generales, resultado, "Los generales deben salir en FIFO tras agotar los Fast-Pass");
    }

    // P11 — Cancelación reduce la cola en exactamente uno
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 11: Cancelación de posición reduce la cola en exactamente uno")
    void eliminarReduceTamanioEnUnoYElementoNoEncontrado(
            @ForAll @Size(min = 1, max = 20) @UniqueElements List<String> items,
            @ForAll @IntRange(min = 0) int semilla) {

        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        for (int i = 0; i < items.size(); i++) cola.encolar(items.get(i), (i % 2 == 0) ? 1 : 2);

        int n = cola.tamanio();
        String objetivo = items.get(semilla % items.size());

        assertTrue(cola.posicionDe(objetivo) != -1);
        assertTrue(cola.eliminar(objetivo));
        assertEquals(n - 1, cola.tamanio(), "El tamaño debe reducirse en exactamente 1");
        assertEquals(-1, cola.posicionDe(objetivo), "posicionDe() debe retornar -1 tras eliminar");
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 11: Cancelación de posición reduce la cola en exactamente uno")
    void eliminarElementoAusenteRetornaFalseYTamanioSinCambio(
            @ForAll @Size(min = 0, max = 20) List<String> items) {

        ColaDePrioridad<String> cola = new ColaDePrioridad<>();
        for (String s : items) cola.encolar(s, 1);
        int antes = cola.tamanio();
        assertFalse(cola.eliminar("ELEMENTO_AUSENTE_GARANTIZADO"));
        assertEquals(antes, cola.tamanio());
    }
}
