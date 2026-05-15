package co.edu.uq.techpark.ds;

import java.io.Serializable;

/**
 * Lista enlazada simple propia.
 * Cada elemento apunta al siguiente, formando una cadena.
 * No usa ninguna colección de Java internamente.
 *
 * Cumple con los Requisitos 15.3, 7.1
 */
public class ListaEnlazada<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ Estado

    /** Primer nodo de la lista. Si es null, la lista está vacía. */
    private Nodo<T> cabeza;

    /** Cantidad de elementos en la lista. */
    private int tamanio;

    // ------------------------------------------------------------------ Métodos públicos

    /** Agrega un elemento al final de la lista. */
    public void agregarAlFinal(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamanio++;
    }

    /** Agrega un elemento al inicio de la lista. */
    public void agregarAlInicio(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        nuevoNodo.siguiente = cabeza;
        cabeza = nuevoNodo;
        tamanio++;
    }

    /** Retorna el primer elemento, o null si la lista está vacía. */
    public T obtenerPrimero() {
        return cabeza == null ? null : cabeza.dato;
    }

    /** Retorna el último elemento, o null si la lista está vacía. */
    public T obtenerUltimo() {
        if (cabeza == null) return null;
        Nodo<T> actual = cabeza;
        while (actual.siguiente != null) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    /**
     * Retorna el elemento en la posición indicada (desde 0).
     * Retorna null si el índice está fuera de rango.
     */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) return null;
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    /** Retorna true si la lista contiene el elemento (usa equals). */
    public boolean contiene(T elemento) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (elemento == null ? actual.dato == null : elemento.equals(actual.dato)) return true;
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Elimina la primera ocurrencia del elemento.
     * Retorna true si lo encontró y eliminó.
     */
    public boolean eliminar(T elemento) {
        if (cabeza == null) return false;
        if (elemento == null ? cabeza.dato == null : elemento.equals(cabeza.dato)) {
            cabeza = cabeza.siguiente;
            tamanio--;
            return true;
        }
        Nodo<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (elemento == null ? actual.siguiente.dato == null : elemento.equals(actual.siguiente.dato)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamanio--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /** Retorna la cantidad de elementos en la lista. */
    public int tamanio() {
        return tamanio;
    }

    /** Retorna true si la lista no tiene elementos. */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /**
     * Crea y retorna una copia de esta lista con los mismos elementos en el mismo orden.
     * No modifica la lista original.
     */
    public ListaEnlazada<T> copiar() {
        ListaEnlazada<T> copia = new ListaEnlazada<>();
        Nodo<T> actual = cabeza;
        while (actual != null) {
            copia.agregarAlFinal(actual.dato);
            actual = actual.siguiente;
        }
        return copia;
    }

    /** Elimina todos los elementos de la lista. */
    public void limpiar() {
        cabeza = null;
        tamanio = 0;
    }

    // ------------------------------------------------------------------ Iterador

    /**
     * Crea un iterador para recorrer la lista elemento por elemento.
     * Uso: for (ListaEnlazada.Iterador<T> it = lista.iterador(); it.tieneSiguiente(); ) { T val = it.siguiente(); }
     */
    public Iterador<T> iterador() {
        return new Iterador<>(cabeza);
    }

    /**
     * Permite recorrer la lista sin exponer su estructura interna.
     * Avanza de nodo en nodo hasta llegar al final.
     */
    public static class Iterador<T> {
        private Nodo<T> actual;

        Iterador(Nodo<T> cabeza) {
            this.actual = cabeza;
        }
        /** Retorna true si hay más elementos por recorrer. */
        public boolean tieneSiguiente() {
            return actual != null;
        }

        /** Retorna el elemento actual y avanza al siguiente. */
        public T siguiente() {
            T dato = actual.dato;
            actual = actual.siguiente;
            return dato;
        }
    }
}
