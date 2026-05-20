package co.edu.uq.techpark.ds;

import java.io.Serializable;

/**
 * Cola de prioridad con dos niveles:
 *   Prioridad 1 = Fast-Pass (se atiende primero)
 *   Prioridad 2 = General  (se atiende después)
 *
 * Dentro del mismo nivel de prioridad, se respeta el orden de llegada (FIFO).
 * No usa java.util.PriorityQueue ni ninguna colección de Java.
 *
 * Cumple con los Requisitos 15.2, 5.1, 5.2
 */
public class ColaDePrioridad<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ Cola FIFO interna por nivel

    /**
     * Cola simple FIFO (primero en entrar, primero en salir).
     * Se usa internamente para cada nivel de prioridad.
     * Usa Nodo<T> del paquete ds.
     */
    private static class ColaSimple<T> implements Serializable {
        private static final long serialVersionUID = 1L;

        Nodo<T> cabeza;
        Nodo<T> cola;
        int tamanio;

        /** Agrega un elemento al final de la cola. */
        void encolar(T elemento) {
            Nodo<T> nuevoNodo = new Nodo<>(elemento);
            if (cola == null) {
                cabeza = cola = nuevoNodo;
            } else {
                cola.siguiente = nuevoNodo;
                cola = nuevoNodo;
            }
            tamanio++;
        }

        /** Retira y retorna el primer elemento de la cola. Retorna null si está vacía. */
        T desencolar() {
            if (cabeza == null) return null;
            T dato = cabeza.dato;
            cabeza = cabeza.siguiente;
            if (cabeza == null) cola = null;
            tamanio--;
            return dato;
        }

        /** Retorna el primer elemento sin retirarlo. Retorna null si está vacía. */
        T verPrimero() {
            return cabeza == null ? null : cabeza.dato;
        }

        /**
         * Elimina la primera ocurrencia del elemento.
         * Retorna true si lo encontró y eliminó.
         */
        boolean eliminar(T elemento) {
            Nodo<T> anterior = null;
            Nodo<T> actual = cabeza;
            while (actual != null) {
                if (actual.dato != null ? actual.dato.equals(elemento) : elemento == null) {
                    if (anterior == null) {
                        cabeza = actual.siguiente;
                    } else {
                        anterior.siguiente = actual.siguiente;
                    }
                    if (actual.siguiente == null) {
                        cola = anterior;
                    }
                    tamanio--;
                    return true;
                }
                anterior = actual;
                actual = actual.siguiente;
            }
            return false;
        }

        /** Retorna true si la cola no tiene elementos. */
        boolean estaVacia() {
            return tamanio == 0;
        }
    }

    // ------------------------------------------------------------------ Estado

    /** Cola para visitantes con tiquete Fast-Pass (prioridad 1). */
    private final ColaSimple<T> colaFastPass = new ColaSimple<>();

    /** Cola para visitantes con tiquete General (prioridad 2). */
    private final ColaSimple<T> colaGeneral = new ColaSimple<>();

    // ------------------------------------------------------------------ Métodos públicos

    /**
     * Agrega un elemento a la cola según su prioridad.
     * Prioridad 1 = Fast-Pass, Prioridad 2 = General.
     */
    public void encolar(T elemento, int prioridad) {
        if (prioridad == 1) {
            colaFastPass.encolar(elemento);
        } else {
            colaGeneral.encolar(elemento);
        }
    }

    /**
     * Retira y retorna el siguiente elemento a atender.
     * Siempre atiende primero a los Fast-Pass antes que a los Generales.
     */
    public T desencolar() {
        if (!colaFastPass.estaVacia()) {
            return colaFastPass.desencolar();
        }
        return colaGeneral.desencolar();
    }

    /**
     * Retorna el siguiente elemento a atender sin retirarlo de la cola.
     * Respeta el orden de prioridad.
     */
    public T verPrimero() {
        if (!colaFastPass.estaVacia()) {
            return colaFastPass.verPrimero();
        }
        return colaGeneral.verPrimero();
    }

    /**
     * Elimina un elemento específico de la cola (cancelación de turno).
     * Retorna true si lo encontró y eliminó.
     */
    public boolean eliminar(T elemento) {
        if (colaFastPass.eliminar(elemento)) return true;
        return colaGeneral.eliminar(elemento);
    }

    /**
     * Retorna la posición del elemento en la cola (empieza en 1).
     * Primero busca en Fast-Pass, luego en General.
     * Retorna -1 si el elemento no está en la cola.
     */
    public int posicionDe(T elemento) {
        int posicion = 1;
        Nodo<T> actual = colaFastPass.cabeza;
        while (actual != null) {
            if (actual.dato != null ? actual.dato.equals(elemento) : elemento == null) {
                return posicion;
            }
            posicion++;
            actual = actual.siguiente;
        }
        actual = colaGeneral.cabeza;
        while (actual != null) {
            if (actual.dato != null ? actual.dato.equals(elemento) : elemento == null) {
                return posicion;
            }
            posicion++;
            actual = actual.siguiente;
        }
        return -1;
    }

    /** Retorna la cantidad total de elementos en la cola (Fast-Pass + General). */
    public int tamanio() {
        return colaFastPass.tamanio + colaGeneral.tamanio;
    }

    /** Retorna true si la cola no tiene ningún elemento. */
    public boolean estaVacia() {
        return colaFastPass.estaVacia() && colaGeneral.estaVacia();
    }

    /**
     * Retorna una copia de todos los elementos en orden de atención
     * (primero los Fast-Pass, luego los Generales) como ListaEnlazada.
     */
    public ListaEnlazada<T> instantanea() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        Nodo<T> actual = colaFastPass.cabeza;
        while (actual != null) {
            resultado.agregarAlFinal(actual.dato);
            actual = actual.siguiente;
        }
        actual = colaGeneral.cabeza;
        while (actual != null) {
            resultado.agregarAlFinal(actual.dato);
            actual = actual.siguiente;
        }
        return resultado;
    }
}
