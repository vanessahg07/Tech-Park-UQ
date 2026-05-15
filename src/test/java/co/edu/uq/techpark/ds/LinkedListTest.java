package co.edu.uq.techpark.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link ListaEnlazada}.
 * Validates: Requirements 15.6
 */
class LinkedListTest {

    private ListaEnlazada<String> lista;

    @BeforeEach
    void setUp() { lista = new ListaEnlazada<>(); }

    // 1. Lista vacía
    @Test void listaVacia_tamanioEsCero()           { assertEquals(0, lista.tamanio()); }
    @Test void listaVacia_estaVaciaEsTrue()         { assertTrue(lista.estaVacia()); }
    @Test void listaVacia_obtenerPrimeroEsNull()    { assertNull(lista.obtenerPrimero()); }
    @Test void listaVacia_obtenerUltimoEsNull()     { assertNull(lista.obtenerUltimo()); }
    @Test void listaVacia_copiarEstaVacia()         { assertTrue(lista.copiar().estaVacia()); }

    // 2. Inserción única
    @Test void insercionUnica_tamanioEsUno()        { lista.agregarAlFinal("A"); assertEquals(1, lista.tamanio()); }
    @Test void insercionUnica_noEstaVacia()         { lista.agregarAlFinal("A"); assertFalse(lista.estaVacia()); }
    @Test void insercionUnica_obtenerPrimero()      { lista.agregarAlFinal("A"); assertEquals("A", lista.obtenerPrimero()); }
    @Test void insercionUnica_obtenerUltimo()       { lista.agregarAlFinal("A"); assertEquals("A", lista.obtenerUltimo()); }

    // 3. Múltiples inserciones
    @Test void multiplesInserciones_tamanio() {
        lista.agregarAlFinal("A"); lista.agregarAlFinal("B"); lista.agregarAlFinal("C");
        assertEquals(3, lista.tamanio());
    }
    @Test void multiplesInserciones_obtenerPrimero() {
        lista.agregarAlFinal("A"); lista.agregarAlFinal("B");
        assertEquals("A", lista.obtenerPrimero());
    }
    @Test void multiplesInserciones_obtenerUltimo() {
        lista.agregarAlFinal("A"); lista.agregarAlFinal("B"); lista.agregarAlFinal("C");
        assertEquals("C", lista.obtenerUltimo());
    }

    // 4. copiar preserva orden
    @Test void copiar_preservaOrdenDeInsercion() {
        lista.agregarAlFinal("X"); lista.agregarAlFinal("Y"); lista.agregarAlFinal("Z");
        ListaEnlazada<String> copia = lista.copiar();
        List<String> resultado = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = copia.iterador(); it.tieneSiguiente(); )
            resultado.add(it.siguiente());
        assertEquals(List.of("X", "Y", "Z"), resultado);
    }

    // 5. limpiar
    @Test void limpiar_reiniciaTamanio()       { lista.agregarAlFinal("A"); lista.agregarAlFinal("B"); lista.limpiar(); assertEquals(0, lista.tamanio()); }
    @Test void limpiar_estaVaciaTrue()         { lista.agregarAlFinal("A"); lista.limpiar(); assertTrue(lista.estaVacia()); }
    @Test void limpiar_obtenerPrimeroNull()    { lista.agregarAlFinal("A"); lista.limpiar(); assertNull(lista.obtenerPrimero()); }
    @Test void limpiar_obtenerUltimoNull()     { lista.agregarAlFinal("A"); lista.limpiar(); assertNull(lista.obtenerUltimo()); }
}
