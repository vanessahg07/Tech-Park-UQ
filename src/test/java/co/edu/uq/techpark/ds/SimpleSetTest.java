package co.edu.uq.techpark.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link ConjuntoSimple}.
 * Validates: Requirements 15.6
 */
class SimpleSetTest {

    private ConjuntoSimple<String> conjunto;

    @BeforeEach
    void setUp() {
        conjunto = new ConjuntoSimple<>();
    }

    @Test void conjuntoVacio_tamanioEsCero()          { assertEquals(0, conjunto.tamanio()); }
    @Test void conjuntoVacio_contieneRetornaFalso()   { assertFalse(conjunto.contiene("algo")); }
    @Test void agregar_primeraInsercion_retornaTrue() { assertTrue(conjunto.agregar("hola")); }
    @Test void agregar_duplicado_retornaFalse()       { conjunto.agregar("hola"); assertFalse(conjunto.agregar("hola")); }
    @Test void agregar_duplicado_tamanioSigueEnUno()  { conjunto.agregar("hola"); conjunto.agregar("hola"); assertEquals(1, conjunto.tamanio()); }
    @Test void contiene_elementoExistente_retornaTrue()    { conjunto.agregar("mundo"); assertTrue(conjunto.contiene("mundo")); }
    @Test void contiene_elementoInexistente_retornaFalso() { conjunto.agregar("mundo"); assertFalse(conjunto.contiene("otro")); }
    @Test void eliminar_elementoExistente_retornaTrue()    { conjunto.agregar("item"); assertTrue(conjunto.eliminar("item")); }
    @Test void eliminar_elementoExistente_tamanioDisminuye(){ conjunto.agregar("item"); conjunto.eliminar("item"); assertEquals(0, conjunto.tamanio()); }
    @Test void eliminar_elementoExistente_contieneRetornaFalso(){ conjunto.agregar("item"); conjunto.eliminar("item"); assertFalse(conjunto.contiene("item")); }
    @Test void eliminar_elementoInexistente_retornaFalso() { conjunto.agregar("item"); assertFalse(conjunto.eliminar("fantasma")); }
    @Test void eliminar_elementoInexistente_tamanioSinCambio(){ conjunto.agregar("item"); conjunto.eliminar("fantasma"); assertEquals(1, conjunto.tamanio()); }
}
