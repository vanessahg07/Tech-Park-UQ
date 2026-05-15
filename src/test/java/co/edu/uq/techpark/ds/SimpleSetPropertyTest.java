package co.edu.uq.techpark.ds;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Size;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 6: Set de favoritos sin duplicados — Validates: Requirements 7.4, 7.6
 */
@Tag("Feature: tech-park-uq, Property 6: Set de favoritos sin duplicados")
class SimpleSetPropertyTest {

    @Property(tries = 100)
    void insercionRepetidaMismoId_tamanioSigueEnUno(
            @ForAll String id,
            @ForAll @Size(min = 1, max = 20) List<String> ignorado) {

        ConjuntoSimple<String> conjunto = new ConjuntoSimple<>();
        for (int i = 0; i < ignorado.size() + 1; i++) conjunto.agregar(id);
        assertEquals(1, conjunto.tamanio());
        assertTrue(conjunto.contiene(id));
    }

    @Property(tries = 100)
    void primerAgregarRetornaTrue_siguientesRetornanFalse(
            @ForAll String id,
            @ForAll @Size(min = 1, max = 20) List<String> ignorado) {

        ConjuntoSimple<String> conjunto = new ConjuntoSimple<>();
        assertTrue(conjunto.agregar(id));
        for (int i = 0; i < ignorado.size(); i++) assertFalse(conjunto.agregar(id));
    }

    @Property(tries = 100)
    void tamanioIgualADistintos_yContieneTodasLasOriginales(
            @ForAll @Size(min = 0, max = 50) List<String> cadenas) {

        ConjuntoSimple<String> conjunto = new ConjuntoSimple<>();
        for (String s : cadenas) conjunto.agregar(s);

        long distintos = cadenas.stream().distinct().count();
        assertEquals((int) distintos, conjunto.tamanio());
        for (String s : cadenas) assertTrue(conjunto.contiene(s));
    }

    @Property(tries = 100)
    void aListaEnlazada_nuncaTieneDuplicados(
            @ForAll @Size(min = 0, max = 50) List<String> cadenas) {

        ConjuntoSimple<String> conjunto = new ConjuntoSimple<>();
        for (String s : cadenas) conjunto.agregar(s);

        ListaEnlazada<String> lista = conjunto.aListaEnlazada();
        // Contar elementos únicos en la lista
        ConjuntoSimple<String> verificador = new ConjuntoSimple<>();
        int total = 0;
        for (ListaEnlazada.Iterador<String> it = lista.iterador(); it.tieneSiguiente(); ) {
            verificador.agregar(it.siguiente());
            total++;
        }
        assertEquals(total, verificador.tamanio(), "aListaEnlazada() no debe contener duplicados");
    }
}
