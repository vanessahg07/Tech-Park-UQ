package co.edu.uq.techpark.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link ColaDePrioridad}.
 * Validates: Requirements 15.6
 */
class PriorityQueueTest {

    private ColaDePrioridad<String> cola;

    @BeforeEach
    void setUp() { cola = new ColaDePrioridad<>(); }

    @Test void colaVacia_tamanioEsCero()        { assertEquals(0, cola.tamanio()); }
    @Test void colaVacia_estaVaciaEsTrue()      { assertTrue(cola.estaVacia()); }
    @Test void colaVacia_desencolarEsNull()     { assertNull(cola.desencolar()); }
    @Test void colaVacia_verPrimeroEsNull()     { assertNull(cola.verPrimero()); }

    @Test void encolarDesencolar_prioridad1AntesPrioridad2() {
        cola.encolar("general", 2);
        cola.encolar("fastpass", 1);
        assertEquals("fastpass", cola.desencolar());
        assertEquals("general", cola.desencolar());
    }

    @Test void encolarDesencolar_variosP1AntesP2() {
        cola.encolar("G1", 2); cola.encolar("F1", 1);
        cola.encolar("G2", 2); cola.encolar("F2", 1);
        assertEquals("F1", cola.desencolar());
        assertEquals("F2", cola.desencolar());
        assertEquals("G1", cola.desencolar());
        assertEquals("G2", cola.desencolar());
    }

    @Test void verPrimero_retornaP1CuandoAmbosPresentes() {
        cola.encolar("general", 2); cola.encolar("fastpass", 1);
        assertEquals("fastpass", cola.verPrimero());
        assertEquals(2, cola.tamanio());
    }

    @Test void fifo_mismaPrioridad1_preservaOrden() {
        cola.encolar("A", 1); cola.encolar("B", 1); cola.encolar("C", 1);
        assertEquals("A", cola.desencolar());
        assertEquals("B", cola.desencolar());
        assertEquals("C", cola.desencolar());
    }

    @Test void fifo_mismaPrioridad2_preservaOrden() {
        cola.encolar("A", 2); cola.encolar("B", 2); cola.encolar("C", 2);
        assertEquals("A", cola.desencolar());
        assertEquals("B", cola.desencolar());
        assertEquals("C", cola.desencolar());
    }

    @Test void eliminar_elementoExistente_retornaTrue()  { cola.encolar("A", 1); cola.encolar("B", 2); assertTrue(cola.eliminar("A")); }
    @Test void eliminar_elementoInexistente_retornaFalse(){ cola.encolar("A", 1); assertFalse(cola.eliminar("Z")); }
    @Test void eliminar_disminuyeTamanioEnUno()          { cola.encolar("A", 1); cola.encolar("B", 2); cola.eliminar("A"); assertEquals(1, cola.tamanio()); }

    @Test void posicionDe_p1EsPrimero() {
        cola.encolar("F", 1); cola.encolar("G", 2);
        assertEquals(1, cola.posicionDe("F"));
        assertEquals(2, cola.posicionDe("G"));
    }

    @Test void posicionDe_retornaMenosUnoTrasEliminar() {
        cola.encolar("A", 1); cola.eliminar("A");
        assertEquals(-1, cola.posicionDe("A"));
    }

    @Test void instantanea_retornaTodosEnOrdenDePrioridad() {
        cola.encolar("G1", 2); cola.encolar("F1", 1);
        cola.encolar("G2", 2); cola.encolar("F2", 1);
        ListaEnlazada<String> snap = cola.instantanea();
        List<String> resultado = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = snap.iterador(); it.tieneSiguiente(); )
            resultado.add(it.siguiente());
        assertEquals(List.of("F1", "F2", "G1", "G2"), resultado);
    }

    @Test void instantanea_noModificaCola() {
        cola.encolar("A", 1); cola.encolar("B", 2);
        cola.instantanea();
        assertEquals(2, cola.tamanio());
    }
}
