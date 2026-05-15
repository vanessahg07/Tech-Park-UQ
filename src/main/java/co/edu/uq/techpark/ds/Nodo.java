package co.edu.uq.techpark.ds;

import java.io.Serializable;

/**
 * Nodo genérico para estructuras de datos enlazadas.
 * Guarda un dato y una referencia al siguiente nodo.
 *
 * Usado por: ListaEnlazada, ColaDePrioridad
 */
public class Nodo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public T dato;
    public Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}
