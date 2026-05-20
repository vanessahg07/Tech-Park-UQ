package co.edu.uq.techpark.ds;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Size;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 5: Corrección de búsqueda en BST — Validates: Requirements 4.2
 */
@Tag("Feature: tech-park-uq, Property 5: Corrección de búsqueda en BST")
class BSTPropertyTest {

    @Property(tries = 100)
    void buscar_retornaValorInsertado(
            @ForAll @Size(min = 1, max = 30) List<String> claves,
            @ForAll @Size(min = 1, max = 30) List<String> valores) {

        int n = Math.min(claves.size(), valores.size());
        Map<String, String> referencia = new HashMap<>();
        ArbolBinarioBusqueda<String, String> arbol = new ArbolBinarioBusqueda<>();
        for (int i = 0; i < n; i++) {
            referencia.put(claves.get(i), valores.get(i));
            arbol.insertar(claves.get(i), valores.get(i));
        }
        for (Map.Entry<String, String> e : referencia.entrySet())
            assertEquals(e.getValue(), arbol.buscar(e.getKey()));
    }

    @Property(tries = 100)
    void buscar_retornaNull_paraClavesNoInsertadas(
            @ForAll @Size(min = 0, max = 20) List<String> claves,
            @ForAll String claveAusente) {

        ArbolBinarioBusqueda<String, String> arbol = new ArbolBinarioBusqueda<>();
        for (String c : claves) {
            Assume.that(!c.equals(claveAusente));
            arbol.insertar(c, "v-" + c);
        }
        assertNull(arbol.buscar(claveAusente));
    }

    @Property(tries = 100)
    void contiene_trueParaInsertadas_falseParaAusente(
            @ForAll @Size(min = 1, max = 30) List<String> claves,
            @ForAll String claveAusente) {

        ArbolBinarioBusqueda<String, String> arbol = new ArbolBinarioBusqueda<>();
        for (String c : claves) {
            Assume.that(!c.equals(claveAusente));
            arbol.insertar(c, "v-" + c);
        }
        for (String c : claves) assertTrue(arbol.contiene(c));
        assertFalse(arbol.contiene(claveAusente));
    }

    @Property(tries = 100)
    void enOrden_retornaValoresEnOrdenAscendente(
            @ForAll @Size(min = 0, max = 30) List<String> claves) {

        ArbolBinarioBusqueda<String, String> arbol = new ArbolBinarioBusqueda<>();
        Map<String, String> referencia = new HashMap<>();
        for (String c : claves) { referencia.put(c, "v-" + c); arbol.insertar(c, "v-" + c); }

        List<String> clavesOrdenadas = new ArrayList<>(referencia.keySet());
        clavesOrdenadas.sort(String::compareTo);
        List<String> esperados = new ArrayList<>();
        for (String c : clavesOrdenadas) esperados.add(referencia.get(c));

        List<String> resultado = new ArrayList<>();
        for (ListaEnlazada.Iterador<String> it = arbol.enOrden().iterador(); it.tieneSiguiente(); )
            resultado.add(it.siguiente());

        assertEquals(esperados, resultado);
    }
}
