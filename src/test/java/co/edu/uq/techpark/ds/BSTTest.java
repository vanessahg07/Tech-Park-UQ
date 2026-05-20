package co.edu.uq.techpark.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link ArbolBinarioBusqueda}.
 * Validates: Requirements 15.6
 */
class BSTTest {

    private ArbolBinarioBusqueda<String, String> arbol;

    @BeforeEach
    void setUp() { arbol = new ArbolBinarioBusqueda<>(); }

    // 1. Árbol vacío
    @Test void arbolVacio_buscarRetornaNull()       { assertNull(arbol.buscar("cualquiera")); }
    @Test void arbolVacio_contieneRetornaFalso()    { assertFalse(arbol.contiene("cualquiera")); }
    @Test void arbolVacio_enOrdenEstaVacio()        { assertTrue(arbol.enOrden().estaVacia()); }

    // 2. Inserción y búsqueda
    @Test void insercionUnica_buscarRetornaValor()  { arbol.insertar("clave", "val"); assertEquals("val", arbol.buscar("clave")); }

    // 3. Múltiples inserciones
    @Test void multiplesInserciones_buscarCadaUno() {
        arbol.insertar("B", "valB"); arbol.insertar("A", "valA"); arbol.insertar("C", "valC");
        assertEquals("valA", arbol.buscar("A"));
        assertEquals("valB", arbol.buscar("B"));
        assertEquals("valC", arbol.buscar("C"));
    }

    // 4. enOrden retorna en orden ascendente
    @Test void enOrden_retornaValoresEnOrdenAscendente() {
        arbol.insertar("C", "valC"); arbol.insertar("A", "valA"); arbol.insertar("B", "valB");
        List<String> resultado = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = arbol.enOrden().iterador(); it.tieneSiguiente(); )
            resultado.add(it.siguiente());
        assertEquals(List.of("valA", "valB", "valC"), resultado);
    }

    // 5. Eliminar nodo hoja
    @Test void eliminar_nodoHoja_eliminadoYBuscarNull() {
        arbol.insertar("B", "valB"); arbol.insertar("A", "valA"); arbol.insertar("C", "valC");
        arbol.eliminar("A");
        assertNull(arbol.buscar("A"));
        assertFalse(arbol.contiene("A"));
        assertEquals("valB", arbol.buscar("B"));
        assertEquals("valC", arbol.buscar("C"));
    }

    // 6. Eliminar nodo con un hijo
    @Test void eliminar_nodoConUnHijo_reestructuracionCorrecta() {
        arbol.insertar("B", "valB"); arbol.insertar("C", "valC"); arbol.insertar("D", "valD");
        arbol.eliminar("C");
        assertNull(arbol.buscar("C"));
        assertEquals("valD", arbol.buscar("D"));
        assertEquals("valB", arbol.buscar("B"));
    }

    // 7. Eliminar nodo con dos hijos
    @Test void eliminar_nodoConDosHijos_sucesorEnOrden() {
        arbol.insertar("D", "valD"); arbol.insertar("B", "valB"); arbol.insertar("F", "valF");
        arbol.insertar("E", "valE"); arbol.insertar("G", "valG");
        arbol.eliminar("D");
        assertNull(arbol.buscar("D"));
        assertEquals("valB", arbol.buscar("B"));
        assertEquals("valE", arbol.buscar("E"));
        assertEquals("valF", arbol.buscar("F"));
        assertEquals("valG", arbol.buscar("G"));
        List<String> resultado = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = arbol.enOrden().iterador(); it.tieneSiguiente(); )
            resultado.add(it.siguiente());
        assertEquals(List.of("valB", "valE", "valF", "valG"), resultado);
    }

    // 8. contiene
    @Test void contiene_trueParaClaveExistente_falseParaAusente() {
        arbol.insertar("X", "valX"); arbol.insertar("Y", "valY");
        assertTrue(arbol.contiene("X")); assertTrue(arbol.contiene("Y")); assertFalse(arbol.contiene("Z"));
    }

    // 9. Clave duplicada actualiza valor
    @Test void claveDuplicada_actualizaValor() {
        arbol.insertar("clave", "original"); arbol.insertar("clave", "actualizado");
        assertEquals("actualizado", arbol.buscar("clave"));
        List<String> lista = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = arbol.enOrden().iterador(); it.tieneSiguiente(); )
            lista.add(it.siguiente());
        assertEquals(1, lista.size());
        assertEquals("actualizado", lista.get(0));
    }
}
