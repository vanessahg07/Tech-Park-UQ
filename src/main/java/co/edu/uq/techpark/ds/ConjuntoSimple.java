package co.edu.uq.techpark.ds;

import java.io.Serializable;

/**
 * Conjunto que no permite duplicados.
 * Internamente usa una ListaEnlazada para guardar los elementos.
 * Si intentas agregar un elemento que ya existe, la operación se ignora.
 *
 * Cumple con los Requisitos 15.5, 7.4
 */
public class ConjuntoSimple<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lista interna donde se guardan los elementos sin repetición. */
    private ListaEnlazada<T> lista;

    /** Cantidad de elementos en el conjunto. */
    private int tamanio;

    public ConjuntoSimple() {
        lista = new ListaEnlazada<>();
        tamanio = 0;
    }

    /**
     * Agrega el elemento al conjunto solo si no existe ya.
     * Retorna true si fue agregado, false si ya existía.
     */
    public boolean agregar(T elemento) {
        if (contiene(elemento)) {
            return false;
        }
        lista.agregarAlFinal(elemento);
        tamanio++;
        return true;
    }

    /** Retorna true si el conjunto contiene el elemento (usa equals). */
    public boolean contiene(T elemento) {
        return lista.contiene(elemento);
    }

    /**
     * Elimina el elemento del conjunto.
     * Retorna true si lo encontró y eliminó, false si no existía.
     */
    public boolean eliminar(T elemento) {
        if (lista.eliminar(elemento)) {
            tamanio--;
            return true;
        }
        return false;
    }

    /** Retorna la cantidad de elementos en el conjunto. */
    public int tamanio() {
        return tamanio;
    }

    /** Retorna true si el conjunto no tiene elementos. */
    public boolean estaVacio() {
        return tamanio == 0;
    }

    /**
     * Retorna una copia de todos los elementos como ListaEnlazada.
     * Útil para recorrer los elementos sin modificar el conjunto.
     */
    public ListaEnlazada<T> aListaEnlazada() {
        return lista.copiar();
    }
}
