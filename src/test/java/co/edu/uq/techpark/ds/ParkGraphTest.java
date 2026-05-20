package co.edu.uq.techpark.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link GrafoDelParque}.
 * Validates: Requirements 15.6
 */
class ParkGraphTest {

    private GrafoDelParque<String> grafo;

    @BeforeEach
    void setUp() { grafo = new GrafoDelParque<>(); }

    // Grafo vacío
    @Test void bfsGrafoVacio_retornaListaVacia()      { assertTrue(grafo.bfs("A").estaVacia()); }
    @Test void existeCaminoGrafoVacio_retornaFalso()  { assertFalse(grafo.existeCamino("A", "B")); }
    @Test void dijkstraGrafoVacio_retornaListaVacia() { assertTrue(grafo.dijkstra("A", "B").estaVacia()); }

    // BFS conectividad
    @Test void bfsGrafoConectado_retornaTodosLosNodos() {
        grafo.agregarArista("A", "B", 1); grafo.agregarArista("B", "C", 1); grafo.agregarArista("C", "D", 1);
        ListaEnlazada<String> visitados = grafo.bfs("A");
        assertEquals(4, visitados.tamanio());
        assertTrue(visitados.contiene("A")); assertTrue(visitados.contiene("B"));
        assertTrue(visitados.contiene("C")); assertTrue(visitados.contiene("D"));
    }

    @Test void bfsGrafoDesconectado_retornaSoloAlcanzables() {
        grafo.agregarArista("A", "B", 1); grafo.agregarArista("C", "D", 1);
        ListaEnlazada<String> visitados = grafo.bfs("A");
        assertEquals(2, visitados.tamanio());
        assertTrue(visitados.contiene("A")); assertTrue(visitados.contiene("B"));
        assertFalse(visitados.contiene("C")); assertFalse(visitados.contiene("D"));
    }

    // Dijkstra camino mínimo
    @Test void dijkstraDistancia_eligeCaminoMinimo() {
        grafo.agregarArista("A", "B", 10); grafo.agregarArista("B", "C", 5); grafo.agregarArista("A", "C", 20);
        assertEquals(15, grafo.distanciaDijkstra("A", "C"));
    }

    @Test void dijkstraRuta_eligeCaminoMinimo() {
        grafo.agregarArista("A", "B", 10); grafo.agregarArista("B", "C", 5); grafo.agregarArista("A", "C", 20);
        ListaEnlazada<String> ruta = grafo.dijkstra("A", "C");
        List<String> resultado = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = ruta.iterador(); it.tieneSiguiente(); )
            resultado.add(it.siguiente());
        assertEquals(List.of("A", "B", "C"), resultado);
    }

    // Grafo desconectado
    @Test void existeCamino_retornaFalsoParaComponentesDesconectados() {
        grafo.agregarArista("A", "B", 1); grafo.agregarArista("C", "D", 1);
        assertFalse(grafo.existeCamino("A", "D")); assertFalse(grafo.existeCamino("A", "C"));
    }

    @Test void dijkstra_retornaVacioParaNodosDesconectados() {
        grafo.agregarArista("A", "B", 1); grafo.agregarArista("C", "D", 1);
        assertTrue(grafo.dijkstra("A", "D").estaVacia());
    }

    // eliminarNodo
    @Test void eliminarNodo_eliminaDelGrafo() {
        grafo.agregarArista("A", "B", 1); grafo.agregarArista("B", "C", 1);
        grafo.eliminarNodo("B");
        assertFalse(grafo.contieneNodo("B"));
    }

    @Test void eliminarNodo_limpiaAristas() {
        grafo.agregarArista("A", "B", 5); grafo.agregarArista("B", "C", 5);
        grafo.eliminarNodo("B");
        assertFalse(grafo.obtenerVecinos("A").contiene("B"));
        assertFalse(grafo.obtenerVecinos("C").contiene("B"));
    }

    @Test void rutaATravesDeNodoEliminado_retornaVacia() {
        grafo.agregarArista("A", "B", 5); grafo.agregarArista("B", "C", 5);
        grafo.eliminarNodo("B");
        assertTrue(grafo.dijkstra("A", "C").estaVacia());
        assertFalse(grafo.existeCamino("A", "C"));
    }

    // obtenerVecinos
    @Test void obtenerVecinos_retornaAdyacentesCorrectos() {
        grafo.agregarArista("A", "B", 1); grafo.agregarArista("A", "C", 2); grafo.agregarArista("A", "D", 3);
        ListaEnlazada<String> vecinos = grafo.obtenerVecinos("A");
        assertEquals(3, vecinos.tamanio());
        assertTrue(vecinos.contiene("B")); assertTrue(vecinos.contiene("C")); assertTrue(vecinos.contiene("D"));
    }

    @Test void obtenerVecinos_nodoDesconocido_retornaVacio() { assertTrue(grafo.obtenerVecinos("Z").estaVacia()); }

    @Test void obtenerVecinos_simetricoParaAristaNoDirigida() {
        grafo.agregarArista("A", "B", 10);
        assertTrue(grafo.obtenerVecinos("A").contiene("B"));
        assertTrue(grafo.obtenerVecinos("B").contiene("A"));
    }
}
