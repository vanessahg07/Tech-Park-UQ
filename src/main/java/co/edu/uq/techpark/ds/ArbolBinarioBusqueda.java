package co.edu.uq.techpark.ds;

import java.io.Serializable;

/**
 * Árbol Binario de Búsqueda (ABB).
 * Permite buscar, insertar y eliminar elementos en tiempo O(log n) promedio.
 * Los elementos con clave menor van a la izquierda y los mayores a la derecha.
 * No usa ninguna estructura de árbol de java.util.
 *
 * Cumple con los Requisitos 15.4, 4.2
 *
 * @param <C> tipo de la clave, debe ser Comparable para poder ordenarse
 * @param <V> tipo del valor asociado a cada clave
 */
public class ArbolBinarioBusqueda<C extends Comparable<C>, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ Nodo interno

    /**
     * Representa un nodo del árbol.
     * Cada nodo tiene una clave, un valor, y referencias a sus hijos izquierdo y derecho.
     */
    private static class Nodo<C, V> implements Serializable {
        private static final long serialVersionUID = 1L;

        C clave;
        V valor;
        Nodo<C, V> izquierdo;
        Nodo<C, V> derecho;

        Nodo(C clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.izquierdo = null;
            this.derecho = null;
        }
    }

    // ------------------------------------------------------------------ Estado

    /** Nodo raíz del árbol. Si es null, el árbol está vacío. */
    private Nodo<C, V> raiz;

    // ------------------------------------------------------------------ Métodos públicos

    /**
     * Inserta un par clave-valor en el árbol.
     * Si la clave ya existe, actualiza su valor.
     */
    public void insertar(C clave, V valor) {
        raiz = insertarRecursivo(raiz, clave, valor);
    }

    private Nodo<C, V> insertarRecursivo(Nodo<C, V> nodo, C clave, V valor) {
        if (nodo == null) {
            return new Nodo<>(clave, valor);
        }
        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, clave, valor);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRecursivo(nodo.derecho, clave, valor);
        } else {
            // La clave ya existe: actualizamos el valor
            nodo.valor = valor;
        }
        return nodo;
    }

    /**
     * Busca y retorna el valor asociado a la clave.
     * Retorna null si la clave no existe en el árbol.
     * Tiempo promedio: O(log n).
     */
    public V buscar(C clave) {
        Nodo<C, V> nodo = buscarNodo(raiz, clave);
        return nodo == null ? null : nodo.valor;
    }

    private Nodo<C, V> buscarNodo(Nodo<C, V> nodo, C clave) {
        if (nodo == null) return null;
        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) return buscarNodo(nodo.izquierdo, clave);
        if (comparacion > 0) return buscarNodo(nodo.derecho, clave);
        return nodo;
    }

    /** Retorna true si el árbol contiene la clave dada. */
    public boolean contiene(C clave) {
        return buscarNodo(raiz, clave) != null;
    }

    /**
     * Retorna todos los valores del árbol en orden ascendente de clave
     * (recorrido en orden: izquierdo → raíz → derecho).
     */
    public ListaEnlazada<V> enOrden() {
        ListaEnlazada<V> resultado = new ListaEnlazada<>();
        enOrdenRecursivo(raiz, resultado);
        return resultado;
    }

    private void enOrdenRecursivo(Nodo<C, V> nodo, ListaEnlazada<V> resultado) {
        if (nodo == null) return;
        enOrdenRecursivo(nodo.izquierdo, resultado);
        resultado.agregarAlFinal(nodo.valor);
        enOrdenRecursivo(nodo.derecho, resultado);
    }

    /**
     * Elimina el nodo con la clave dada.
     * Maneja los tres casos: nodo hoja, nodo con un hijo, nodo con dos hijos.
     * No hace nada si la clave no existe.
     */
    public void eliminar(C clave) {
        raiz = eliminarRecursivo(raiz, clave);
    }

    private Nodo<C, V> eliminarRecursivo(Nodo<C, V> nodo, C clave) {
        if (nodo == null) return null;

        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, clave);
        } else if (comparacion > 0) {
            nodo.derecho = eliminarRecursivo(nodo.derecho, clave);
        } else {
            // Nodo encontrado: manejamos los tres casos
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho == null) return nodo.izquierdo;

            // Tiene dos hijos: reemplazamos con el sucesor (el menor del subárbol derecho)
            Nodo<C, V> sucesor = encontrarMinimo(nodo.derecho);
            nodo.clave = sucesor.clave;
            nodo.valor = sucesor.valor;
            nodo.derecho = eliminarRecursivo(nodo.derecho, sucesor.clave);
        }
        return nodo;
    }

    /** Encuentra el nodo con la clave más pequeña en el subárbol dado. */
    private Nodo<C, V> encontrarMinimo(Nodo<C, V> nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    /** Retorna true si el árbol no tiene elementos. */
    public boolean estaVacio() {
        return raiz == null;
    }
}
