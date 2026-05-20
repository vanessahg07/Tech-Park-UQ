package co.edu.uq.techpark.ds;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 7: Ruta óptima minimiza distancia total — Validates: Requirements 6.2
 * Property 12: Nodos cerrados excluidos de rutas óptimas — Validates: Requirements 6.6
 */
class ParkGraphPropertyTest {

    // P7 — Dijkstra minimiza distancia
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 7: Ruta optima minimiza distancia total")
    void dijkstraDistanciaEsMinima(
            @ForAll @IntRange(min = 1, max = 100) int wAB,
            @ForAll @IntRange(min = 1, max = 100) int wBC,
            @ForAll @IntRange(min = 1, max = 100) int wAC) {

        GrafoDelParque<String> grafo = new GrafoDelParque<>();
        grafo.agregarArista("A", "B", wAB); grafo.agregarArista("B", "C", wBC); grafo.agregarArista("A", "C", wAC);
        assertEquals(Math.min(wAC, wAB + wBC), grafo.distanciaDijkstra("A", "C"));
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 7: Ruta optima minimiza distancia total")
    void dijkstraDistanciaEsSimetrica(
            @ForAll @IntRange(min = 1, max = 100) int wAB,
            @ForAll @IntRange(min = 1, max = 100) int wBC,
            @ForAll @IntRange(min = 1, max = 100) int wAC) {

        GrafoDelParque<String> grafo = new GrafoDelParque<>();
        grafo.agregarArista("A", "B", wAB); grafo.agregarArista("B", "C", wBC); grafo.agregarArista("A", "C", wAC);
        assertEquals(grafo.distanciaDijkstra("A", "C"), grafo.distanciaDijkstra("C", "A"));
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 7: Ruta optima minimiza distancia total")
    void pesoDeLaRutaCoincideConDistanciaDijkstra(
            @ForAll @IntRange(min = 1, max = 100) int wAB,
            @ForAll @IntRange(min = 1, max = 100) int wBC,
            @ForAll @IntRange(min = 1, max = 100) int wAC) {

        GrafoDelParque<String> grafo = new GrafoDelParque<>();
        grafo.agregarArista("A", "B", wAB); grafo.agregarArista("B", "C", wBC); grafo.agregarArista("A", "C", wAC);

        ListaEnlazada<String> ruta = grafo.dijkstra("A", "C");
        int distancia = grafo.distanciaDijkstra("A", "C");

        assertFalse(ruta.estaVacia());
        assertEquals("A", ruta.obtenerPrimero());
        assertEquals("C", ruta.obtenerUltimo());
        assertEquals(distancia, calcularPesoRuta(grafo, ruta));
    }

    // P12 — Nodos eliminados excluidos de rutas
    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 12: Nodos cerrados excluidos de rutas optimas")
    void nodoIntermediarioEliminado_noAparece(
            @ForAll @IntRange(min = 1, max = 50) int wAB,
            @ForAll @IntRange(min = 1, max = 50) int wBC,
            @ForAll @IntRange(min = 1, max = 50) int wCD) {

        GrafoDelParque<String> grafo = new GrafoDelParque<>();
        grafo.agregarArista("A", "B", wAB); grafo.agregarArista("B", "C", wBC); grafo.agregarArista("C", "D", wCD);
        grafo.eliminarNodo("B");
        assertFalse(grafo.contieneNodo("B"));
        assertTrue(grafo.dijkstra("A", "D").estaVacia());
        assertTrue(grafo.dijkstra("A", "C").estaVacia());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 12: Nodos cerrados excluidos de rutas optimas")
    void eliminarOrigenODestino_retornaVacio(
            @ForAll @IntRange(min = 1, max = 50) int wAB,
            @ForAll @IntRange(min = 1, max = 50) int wBC) {

        GrafoDelParque<String> g1 = new GrafoDelParque<>();
        g1.agregarArista("A", "B", wAB); g1.agregarArista("B", "C", wBC);
        g1.eliminarNodo("A");
        assertTrue(g1.dijkstra("A", "C").estaVacia());

        GrafoDelParque<String> g2 = new GrafoDelParque<>();
        g2.agregarArista("A", "B", wAB); g2.agregarArista("B", "C", wBC);
        g2.eliminarNodo("C");
        assertTrue(g2.dijkstra("A", "C").estaVacia());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 12: Nodos cerrados excluidos de rutas optimas")
    void rutaAlternativaExcluyeNodoEliminado(
            @ForAll @IntRange(min = 1, max = 50) int wAB,
            @ForAll @IntRange(min = 1, max = 50) int wBC,
            @ForAll @IntRange(min = 1, max = 50) int wAC) {

        GrafoDelParque<String> grafo = new GrafoDelParque<>();
        grafo.agregarArista("A", "B", wAB); grafo.agregarArista("B", "C", wBC); grafo.agregarArista("A", "C", wAC);
        grafo.eliminarNodo("B");

        ListaEnlazada<String> ruta = grafo.dijkstra("A", "C");
        assertFalse(ruta.estaVacia());
        assertFalse(ruta.contiene("B"));
        assertEquals("A", ruta.obtenerPrimero());
        assertEquals("C", ruta.obtenerUltimo());
    }

    // Helper
    private int calcularPesoRuta(GrafoDelParque<String> grafo, ListaEnlazada<String> ruta) {
        List<String> nodos = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = ruta.iterador(); it.tieneSiguiente(); )
            nodos.add(it.siguiente());
        int total = 0;
        for (int i = 0; i < nodos.size() - 1; i++) {
            String desde = nodos.get(i), hasta = nodos.get(i + 1);
            for (ListaEnlazada.Iterador<GrafoDelParque.Arista<String>> it = grafo.obtenerAristas(desde).iterador(); it.tieneSiguiente(); ) {
                GrafoDelParque.Arista<String> arista = it.siguiente();
                if (arista.destino.equals(hasta)) { total += arista.pesoEnMetros; break; }
            }
        }
        return total;
    }
}
