package co.edu.uq.techpark.ds;

import java.io.Serializable;

/**
 * Grafo no dirigido con pesos.
 * Representa el mapa del parque: cada nodo es una atracción
 * y cada arista es un camino con distancia en metros.
 *
 * Implementa BFS para verificar si existe un camino entre dos nodos,
 * y Dijkstra para encontrar la ruta más corta.
 *
 * No usa ninguna colección de Java. Usa ArbolBinarioBusqueda como mapa
 * y ListaEnlazada como lista de aristas.
 *
 * Cumple con los Requisitos 15.1, 6.1, 6.2, 6.3
 */
public class GrafoDelParque<T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ Arista

    /**
     * Representa una conexión entre dos nodos del grafo.
     * Guarda el nodo destino y el peso (distancia en metros).
     */
    public static class Arista<T> implements Serializable {
        private static final long serialVersionUID = 1L;

        public final T destino;
        public final int pesoEnMetros;

        public Arista(T destino, int pesoEnMetros) {
            this.destino = destino;
            this.pesoEnMetros = pesoEnMetros;
        }
    }

    // ------------------------------------------------------------------ NodoConDistancia (para Dijkstra)

    /**
     * Nodo auxiliar usado en el montículo mínimo de Dijkstra.
     * Guarda el nodo y la distancia acumulada desde el origen.
     */
    private static class NodoConDistancia<T> implements Comparable<NodoConDistancia<T>>, Serializable {
        private static final long serialVersionUID = 1L;

        final T nodo;
        final int distancia;

        NodoConDistancia(T nodo, int distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoConDistancia<T> otro) {
            return Integer.compare(this.distancia, otro.distancia);
        }
    }

    // ------------------------------------------------------------------ MonticuloMinimo (min-heap para Dijkstra)

    /**
     * Montículo mínimo (min-heap) implementado con un arreglo.
     * Permite obtener siempre el elemento con menor valor en O(log n).
     * Se usa internamente en Dijkstra para elegir el nodo más cercano.
     */
    private static class MonticuloMinimo<E extends Comparable<E>> implements Serializable {
        private static final long serialVersionUID = 1L;

        private Object[] datos;
        private int tamanio;

        MonticuloMinimo(int capacidadInicial) {
            datos = new Object[capacidadInicial];
            tamanio = 0;
        }

        /** Agrega un elemento al montículo y lo reubica para mantener la propiedad de heap. */
        void agregar(E elemento) {
            if (tamanio == datos.length) {
                Object[] masGrande = new Object[datos.length * 2];
                System.arraycopy(datos, 0, masGrande, 0, tamanio);
                datos = masGrande;
            }
            datos[tamanio] = elemento;
            subirHaciaArriba(tamanio);
            tamanio++;
        }

        /** Retira y retorna el elemento con menor valor. */
        @SuppressWarnings("unchecked")
        E retirarMinimo() {
            if (tamanio == 0) return null;
            E minimo = (E) datos[0];
            tamanio--;
            datos[0] = datos[tamanio];
            datos[tamanio] = null;
            if (tamanio > 0) bajarHaciaAbajo(0);
            return minimo;
        }

        boolean estaVacio() { return tamanio == 0; }

        @SuppressWarnings("unchecked")
        private void subirHaciaArriba(int indice) {
            while (indice > 0) {
                int indicePadre = (indice - 1) / 2;
                if (((E) datos[indice]).compareTo((E) datos[indicePadre]) < 0) {
                    Object temporal = datos[indice];
                    datos[indice] = datos[indicePadre];
                    datos[indicePadre] = temporal;
                    indice = indicePadre;
                } else break;
            }
        }

        @SuppressWarnings("unchecked")
        private void bajarHaciaAbajo(int indice) {
            while (true) {
                int hijoIzquierdo = 2 * indice + 1;
                int hijoDerecho = 2 * indice + 2;
                int indiceMenor = indice;
                if (hijoIzquierdo < tamanio && ((E) datos[hijoIzquierdo]).compareTo((E) datos[indiceMenor]) < 0)
                    indiceMenor = hijoIzquierdo;
                if (hijoDerecho < tamanio && ((E) datos[hijoDerecho]).compareTo((E) datos[indiceMenor]) < 0)
                    indiceMenor = hijoDerecho;
                if (indiceMenor == indice) break;
                Object temporal = datos[indice];
                datos[indice] = datos[indiceMenor];
                datos[indiceMenor] = temporal;
                indice = indiceMenor;
            }
        }
    }

    // ------------------------------------------------------------------ EntradaDistancia (para Dijkstra)

    /**
     * Guarda la distancia mínima conocida, el nodo anterior en el camino,
     * y si el nodo ya fue procesado definitivamente por Dijkstra.
     */
    private static class EntradaDistancia<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        int distancia;
        T anterior;
        boolean procesado;

        EntradaDistancia(int distancia) { this.distancia = distancia; }
    }

    // ------------------------------------------------------------------ Estado del grafo

    /**
     * Lista de adyacencia: para cada nodo guarda la lista de aristas que salen de él.
     * Usamos un ArbolBinarioBusqueda como mapa: nodo → lista de aristas.
     */
    private final ArbolBinarioBusqueda<T, ListaEnlazada<Arista<T>>> listaDeAdyacencia;

    /** Lista de todos los nodos del grafo, en orden de inserción. */
    private ListaEnlazada<T> nodos;

    public GrafoDelParque() {
        this.listaDeAdyacencia = new ArbolBinarioBusqueda<>();
        this.nodos = new ListaEnlazada<>();
    }

    // ------------------------------------------------------------------ Modificación del grafo

    /** Agrega un nodo al grafo si no existe ya. */
    public void agregarNodo(T nodo) {
        if (!listaDeAdyacencia.contiene(nodo)) {
            listaDeAdyacencia.insertar(nodo, new ListaEnlazada<Arista<T>>());
            nodos.agregarAlFinal(nodo);
        }
    }

    /**
     * Agrega una arista no dirigida (bidireccional) entre dos nodos con el peso dado.
     * Si los nodos no existen, los crea automáticamente.
     */
    public void agregarArista(T desde, T hasta, int pesoEnMetros) {
        agregarNodo(desde);
        agregarNodo(hasta);
        listaDeAdyacencia.buscar(desde).agregarAlFinal(new Arista<>(hasta, pesoEnMetros));
        listaDeAdyacencia.buscar(hasta).agregarAlFinal(new Arista<>(desde, pesoEnMetros));
    }

    /**
     * Elimina un nodo y todas sus aristas del grafo.
     * Se usa para excluir atracciones cerradas del cálculo de rutas.
     */
    public void eliminarNodo(T nodo) {
        if (!listaDeAdyacencia.contiene(nodo)) return;
        // Eliminamos todas las aristas que apuntan a este nodo desde otros nodos
        for (ListaEnlazada.Iterador<T> it = nodos.iterador(); it.tieneSiguiente(); ) {
            T otroNodo = it.siguiente();
            if (otroNodo.equals(nodo)) continue;
            ListaEnlazada<Arista<T>> listaAristas = listaDeAdyacencia.buscar(otroNodo);
            if (listaAristas == null) continue;
            ListaEnlazada<Arista<T>> filtrada = new ListaEnlazada<>();
            for (ListaEnlazada.Iterador<Arista<T>> ei = listaAristas.iterador(); ei.tieneSiguiente(); ) {
                Arista<T> arista = ei.siguiente();
                if (!arista.destino.equals(nodo)) filtrada.agregarAlFinal(arista);
            }
            listaDeAdyacencia.insertar(otroNodo, filtrada);
        }
        listaDeAdyacencia.eliminar(nodo);
        nodos.eliminar(nodo);
    }

    /** Elimina la arista entre dos nodos (en ambas direcciones). */
    public void eliminarArista(T desde, T hasta) {
        eliminarAristaDirigida(desde, hasta);
        eliminarAristaDirigida(hasta, desde);
    }

    private void eliminarAristaDirigida(T desde, T hasta) {
        ListaEnlazada<Arista<T>> listaAristas = listaDeAdyacencia.buscar(desde);
        if (listaAristas == null) return;
        ListaEnlazada<Arista<T>> filtrada = new ListaEnlazada<>();
        for (ListaEnlazada.Iterador<Arista<T>> it = listaAristas.iterador(); it.tieneSiguiente(); ) {
            Arista<T> arista = it.siguiente();
            if (!arista.destino.equals(hasta)) filtrada.agregarAlFinal(arista);
        }
        listaDeAdyacencia.insertar(desde, filtrada);
    }

    // ------------------------------------------------------------------ Consultas

    /** Retorna true si el grafo contiene el nodo dado. */
    public boolean contieneNodo(T nodo) {
        return listaDeAdyacencia.contiene(nodo);
    }

    /** Retorna la cantidad de nodos en el grafo. */
    public int cantidadDeNodos() {
        return nodos.tamanio();
    }

    /** Retorna todos los nodos del grafo como ListaEnlazada. */
    public ListaEnlazada<T> obtenerNodos() {
        return nodos.copiar();
    }

    /** Retorna los nodos vecinos (destinos de las aristas) del nodo dado. */
    public ListaEnlazada<T> obtenerVecinos(T nodo) {
        ListaEnlazada<T> vecinos = new ListaEnlazada<>();
        ListaEnlazada<Arista<T>> listaAristas = listaDeAdyacencia.buscar(nodo);
        if (listaAristas == null) return vecinos;
        for (ListaEnlazada.Iterador<Arista<T>> it = listaAristas.iterador(); it.tieneSiguiente(); ) {
            vecinos.agregarAlFinal(it.siguiente().destino);
        }
        return vecinos;
    }

    /** Retorna todas las aristas que salen del nodo dado. */
    public ListaEnlazada<Arista<T>> obtenerAristas(T nodo) {
        ListaEnlazada<Arista<T>> listaAristas = listaDeAdyacencia.buscar(nodo);
        if (listaAristas == null) return new ListaEnlazada<>();
        return listaAristas.copiar();
    }

    // ------------------------------------------------------------------ BFS

    /**
     * Recorrido BFS (Búsqueda en Anchura) desde el nodo de inicio.
     * Visita primero los nodos más cercanos antes de ir más lejos.
     * Retorna los nodos visitados en orden de visita.
     */
    public ListaEnlazada<T> bfs(T inicio) {
        ListaEnlazada<T> visitados = new ListaEnlazada<>();
        if (!listaDeAdyacencia.contiene(inicio)) return visitados;

        ArbolBinarioBusqueda<T, Boolean> yaVisto = new ArbolBinarioBusqueda<>();
        ListaEnlazada<T> colaDeBFS = new ListaEnlazada<>();

        yaVisto.insertar(inicio, true);
        colaDeBFS.agregarAlFinal(inicio);

        while (!colaDeBFS.estaVacia()) {
            T actual = colaDeBFS.obtenerPrimero();
            colaDeBFS.eliminar(actual);
            visitados.agregarAlFinal(actual);

            ListaEnlazada<Arista<T>> listaAristas = listaDeAdyacencia.buscar(actual);
            if (listaAristas == null) continue;
            for (ListaEnlazada.Iterador<Arista<T>> it = listaAristas.iterador(); it.tieneSiguiente(); ) {
                T destino = it.siguiente().destino;
                if (!yaVisto.contiene(destino)) {
                    yaVisto.insertar(destino, true);
                    colaDeBFS.agregarAlFinal(destino);
                }
            }
        }
        return visitados;
    }

    /**
     * Retorna true si existe al menos un camino entre los dos nodos.
     * Usa BFS para verificar la conectividad.
     */
    public boolean existeCamino(T desde, T hasta) {
        if (!listaDeAdyacencia.contiene(desde) || !listaDeAdyacencia.contiene(hasta)) return false;
        if (desde.equals(hasta)) return true;
        return bfs(desde).contiene(hasta);
    }

    // ------------------------------------------------------------------ Dijkstra

    /**
     * Calcula la ruta más corta entre dos nodos usando el algoritmo de Dijkstra.
     * Retorna la secuencia de nodos del camino más corto como ListaEnlazada.
     * Retorna una lista vacía si no existe camino.
     */
    public ListaEnlazada<T> dijkstra(T desde, T hasta) {
        if (!listaDeAdyacencia.contiene(desde) || !listaDeAdyacencia.contiene(hasta))
            return new ListaEnlazada<>();

        ArbolBinarioBusqueda<T, EntradaDistancia<T>> mapaDistancias = construirMapaDeDistancias(desde);
        return reconstruirCamino(mapaDistancias, desde, hasta);
    }

    /**
     * Calcula la distancia total en metros del camino más corto entre dos nodos.
     * Retorna Integer.MAX_VALUE si no existe camino.
     */
    public int distanciaDijkstra(T desde, T hasta) {
        if (!listaDeAdyacencia.contiene(desde) || !listaDeAdyacencia.contiene(hasta))
            return Integer.MAX_VALUE;
        if (desde.equals(hasta)) return 0;

        ArbolBinarioBusqueda<T, EntradaDistancia<T>> mapaDistancias = construirMapaDeDistancias(desde);
        EntradaDistancia<T> entrada = mapaDistancias.buscar(hasta);
        return entrada == null ? Integer.MAX_VALUE : entrada.distancia;
    }

    /**
     * Ejecuta el algoritmo de Dijkstra desde el nodo origen.
     * Construye un mapa con la distancia mínima conocida a cada nodo
     * y el nodo anterior en el camino más corto.
     */
    @SuppressWarnings("unchecked")
    private ArbolBinarioBusqueda<T, EntradaDistancia<T>> construirMapaDeDistancias(T origen) {
        ArbolBinarioBusqueda<T, EntradaDistancia<T>> mapaDistancias = new ArbolBinarioBusqueda<>();

        // Inicializamos todas las distancias como infinito
        for (ListaEnlazada.Iterador<T> it = nodos.iterador(); it.tieneSiguiente(); ) {
            mapaDistancias.insertar(it.siguiente(), new EntradaDistancia<T>(Integer.MAX_VALUE));
        }
        mapaDistancias.buscar(origen).distancia = 0;

        MonticuloMinimo<NodoConDistancia<T>> monticuloMinimo = new MonticuloMinimo<>(nodos.tamanio() + 1);
        monticuloMinimo.agregar(new NodoConDistancia<>(origen, 0));

        while (!monticuloMinimo.estaVacio()) {
            NodoConDistancia<T> actual = monticuloMinimo.retirarMinimo();
            T nodoActual = actual.nodo;

            EntradaDistancia<T> entradaActual = mapaDistancias.buscar(nodoActual);
            if (entradaActual == null || entradaActual.procesado) continue;
            entradaActual.procesado = true;

            ListaEnlazada<Arista<T>> listaAristas = listaDeAdyacencia.buscar(nodoActual);
            if (listaAristas == null) continue;

            for (ListaEnlazada.Iterador<Arista<T>> it = listaAristas.iterador(); it.tieneSiguiente(); ) {
                Arista<T> arista = it.siguiente();
                T vecino = arista.destino;
                EntradaDistancia<T> entradaVecino = mapaDistancias.buscar(vecino);
                if (entradaVecino == null || entradaVecino.procesado) continue;

                int nuevaDistancia = entradaActual.distancia + arista.pesoEnMetros;
                if (nuevaDistancia < entradaVecino.distancia) {
                    entradaVecino.distancia = nuevaDistancia;
                    entradaVecino.anterior = nodoActual;
                    monticuloMinimo.agregar(new NodoConDistancia<>(vecino, nuevaDistancia));
                }
            }
        }
        return mapaDistancias;
    }

    /**
     * Reconstruye el camino desde el origen hasta el destino
     * usando el mapa de distancias calculado por Dijkstra.
     * Retorna la lista de nodos en orden desde origen hasta destino.
     */
    private ListaEnlazada<T> reconstruirCamino(
            ArbolBinarioBusqueda<T, EntradaDistancia<T>> mapaDistancias, T desde, T hasta) {

        EntradaDistancia<T> entradaDestino = mapaDistancias.buscar(hasta);
        if (entradaDestino == null || (entradaDestino.anterior == null && !desde.equals(hasta)))
            return new ListaEnlazada<>();

        // Construimos el camino al revés (desde el destino hacia el origen)
        ListaEnlazada<T> caminoInvertido = new ListaEnlazada<>();
        T paso = hasta;
        while (paso != null) {
            caminoInvertido.agregarAlFinal(paso);
            EntradaDistancia<T> entrada = mapaDistancias.buscar(paso);
            paso = (entrada != null) ? entrada.anterior : null;
        }

        // Invertimos para obtener el orden correcto (origen → destino)
        ListaEnlazada<T> camino = new ListaEnlazada<>();
        for (int i = caminoInvertido.tamanio() - 1; i >= 0; i--) {
            camino.agregarAlFinal(caminoInvertido.obtener(i));
        }

        if (camino.estaVacia() || !camino.obtenerPrimero().equals(desde)) return new ListaEnlazada<>();
        return camino;
    }
}
