package co.edu.uq.techpark.service;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.ds.GrafoDelParque;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.EstadoAtraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.util.ExcepcionDelParque;

/**
 * Servicio para calcular rutas entre atracciones usando el grafo del parque.
 * Excluye atracciones CERRADAS y EN_MANTENIMIENTO del cálculo de rutas.
 * Requisitos: 6.2, 6.3, 6.4, 6.6
 */
public class ServicioDeRutas {

    public static class ResultadoDeRuta {
        public final ListaEnlazada<String> camino;
        public final int distanciaTotalEnMetros;

        public ResultadoDeRuta(ListaEnlazada<String> camino, int distanciaTotalEnMetros) {
            this.camino = camino;
            this.distanciaTotalEnMetros = distanciaTotalEnMetros;
        }
    }

    public static ResultadoDeRuta calcularRuta(String idOrigen, String idDestino, ContextoDelParque contexto)
            throws ExcepcionDelParque {

        // Construir una copia del grafo usando solo nuestras estructuras
        GrafoDelParque<String> copia = new GrafoDelParque<>();
        ListaEnlazada<String> todosLosNodos = contexto.getGrafoDelParque().obtenerNodos();
        for (ListaEnlazada.Iterador<String> it = todosLosNodos.iterador(); it.tieneSiguiente(); ) {
            copia.agregarNodo(it.siguiente());
        }
        for (ListaEnlazada.Iterador<String> it = todosLosNodos.iterador(); it.tieneSiguiente(); ) {
            String nodo = it.siguiente();
            ListaEnlazada<GrafoDelParque.Arista<String>> aristas = contexto.getGrafoDelParque().obtenerAristas(nodo);
            for (ListaEnlazada.Iterador<GrafoDelParque.Arista<String>> ei = aristas.iterador(); ei.tieneSiguiente(); ) {
                GrafoDelParque.Arista<String> arista = ei.siguiente();
                // Solo agregar cada arista una vez (evitar duplicados en grafo no dirigido)
                if (nodo.compareTo(arista.destino) < 0) {
                    copia.agregarArista(nodo, arista.destino, arista.pesoEnMetros);
                }
            }
        }

        // Eliminar atracciones cerradas y en mantenimiento del grafo de copia
        ListaEnlazada<Atraccion> atracciones = contexto.getAtraccionesPorId().enOrden();
        for (ListaEnlazada.Iterador<Atraccion> it = atracciones.iterador(); it.tieneSiguiente(); ) {
            Atraccion atraccion = it.siguiente();
            EstadoAtraccion estado = atraccion.getEstado();
            if (estado == EstadoAtraccion.CERRADA || estado == EstadoAtraccion.EN_MANTENIMIENTO) {
                copia.eliminarNodo(atraccion.getId());
            }
        }

        if (!copia.contieneNodo(idOrigen) || !copia.contieneNodo(idDestino)) {
            throw new ExcepcionDelParque("No hay ruta disponible: origen o destino no accesible");
        }
        if (!copia.existeCamino(idOrigen, idDestino)) {
            throw new ExcepcionDelParque("No hay ruta disponible entre las atracciones");
        }

        return new ResultadoDeRuta(copia.dijkstra(idOrigen, idDestino), copia.distanciaDijkstra(idOrigen, idDestino));
    }
}
