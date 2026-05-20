package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.ds.ArbolBinarioBusqueda;
import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Zona;
import co.edu.uq.techpark.service.ServicioDeRutas;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 6.2, 6.3, 6.4 */
public class ServletDelMapa extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        construirAtributosDelMapa(req, contexto, null, null);
        req.getRequestDispatcher("/WEB-INF/views/visitor/map.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        String idOrigen  = req.getParameter("origin");
        String idDestino = req.getParameter("destination");
        ListaEnlazada<String> camino = null;
        Integer distanciaRuta = null;
        try {
            ServicioDeRutas.ResultadoDeRuta resultado = ServicioDeRutas.calcularRuta(idOrigen, idDestino, contexto);
            camino = resultado.camino;
            distanciaRuta = resultado.distanciaTotalEnMetros;
            req.setAttribute("route", camino);
            req.setAttribute("routeDistance", distanciaRuta);
        } catch (ExcepcionDelParque e) {
            req.setAttribute("error", e.getMessage());
        }
        construirAtributosDelMapa(req, contexto, camino, distanciaRuta);
        req.getRequestDispatcher("/WEB-INF/views/visitor/map.jsp").forward(req, resp);
    }

    private void construirAtributosDelMapa(HttpServletRequest req, ContextoDelParque contexto,
                                           ListaEnlazada<String> camino, Integer distanciaRuta) {

        ListaEnlazada<Atraccion> atracciones = contexto.getAtraccionesPorId().enOrden();
        ListaEnlazada<DatosNodo> nodos = new ListaEnlazada<>();
        ListaEnlazada<DatosArista> aristas = new ListaEnlazada<>();

        // Agrupar atracciones por zona
        ArbolBinarioBusqueda<String, ListaEnlazada<Atraccion>> porZona = new ArbolBinarioBusqueda<>();
        for (ListaEnlazada.Iterador<Atraccion> it = atracciones.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            String idZona = a.getIdZona() != null ? a.getIdZona() : "unknown";
            ListaEnlazada<Atraccion> lista = porZona.buscar(idZona);
            if (lista == null) {
                lista = new ListaEnlazada<>();
                porZona.insertar(idZona, lista);
            }
            lista.agregarAlFinal(a);
        }

        // Asignar regiones SVG por zona según palabras clave del nombre
        // Canvas SVG: viewBox="0 0 900 560"
        // Zonas en SVG:  Aventura Extrema  x=30..300  y=30..240   → cx=165, cy=135
        //                Acuática          x=600..870 y=30..240   → cx=735, cy=135
        //                Familiar          x=180..720 y=350..525  → cx=450, cy=437
        // Arreglo de región: [cx, cy, halfW, halfH]
        ArbolBinarioBusqueda<String, int[]> regionesPorZona = new ArbolBinarioBusqueda<>();

        // Primera pasada: asignar regiones por nombre de zona
        ListaEnlazada<Zona> listaZonas = contexto.getZonas().enOrden();
        for (ListaEnlazada.Iterador<Zona> it = listaZonas.iterador(); it.tieneSiguiente(); ) {
            Zona z = it.siguiente();
            // Normalizar: quitar tildes y pasar a minúsculas para comparación confiable
            String nombreZona = z.getNombre() != null
                    ? java.text.Normalizer.normalize(z.getNombre(), java.text.Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase()
                    : "";
            int[] region;
            if (nombreZona.contains("acua") || nombreZona.contains("agua") || nombreZona.contains("aqua")) {
                region = new int[]{735, 135, 100, 65};
            } else if (nombreZona.contains("familiar") || nombreZona.contains("familia")) {
                region = new int[]{450, 437, 190, 55};
            } else {
                // Aventura Extrema u otra zona → esquina superior izquierda
                region = new int[]{165, 135, 100, 65};
            }
            regionesPorZona.insertar(z.getId(), region);
        }

        // Segunda pasada: ubicar nodos dentro de su región de zona
        ListaEnlazada<Zona> zonasOrdenadas = contexto.getZonas().enOrden();
        for (ListaEnlazada.Iterador<Zona> zit = zonasOrdenadas.iterador(); zit.tieneSiguiente(); ) {
            Zona z = zit.siguiente();
            String idZona = z.getId();
            ListaEnlazada<Atraccion> atraccionesZona = porZona.buscar(idZona);
            if (atraccionesZona == null || atraccionesZona.estaVacia()) continue;

            int[] region = regionesPorZona.buscar(idZona);
            if (region == null) continue;

            int cx = region[0], cy = region[1], hw = region[2], hh = region[3];
            int cantidad = atraccionesZona.tamanio();
            int i = 0;
            for (ListaEnlazada.Iterador<Atraccion> ait = atraccionesZona.iterador(); ait.tieneSiguiente(); i++) {
                Atraccion a = ait.siguiente();
                int x, y;
                if (cantidad == 1) {
                    x = cx;
                    y = cy;
                } else if (cantidad == 2) {
                    x = cx + (i == 0 ? -hw / 2 : hw / 2);
                    y = cy;
                } else {
                    // Distribución en cuadrícula
                    int cols = (int) Math.ceil(Math.sqrt(cantidad));
                    int rows = (int) Math.ceil((double) cantidad / cols);
                    int col = i % cols;
                    int row = i / cols;
                    x = cols > 1 ? cx - hw + col * (2 * hw / (cols - 1)) : cx;
                    y = rows > 1 ? cy - hh + row * (2 * hh / (rows - 1)) : cy;
                }

                String tipo = a.getTipo() != null ? a.getTipo().name() : "";
                String icono;
                if ("ACUATICA".equals(tipo))              icono = "💧";
                else if ("MECANICA_DE_ALTURA".equals(tipo)) icono = "🎢";
                else if ("ESPECTACULO".equals(tipo))      icono = "🎭";
                else if ("MECANICA".equals(tipo))         icono = "🎡";
                else                                      icono = "🎪";

                nodos.agregarAlFinal(new DatosNodo(
                        a.getId(), a.getNombre(),
                        a.getEstado() != null ? a.getEstado().name() : "CERRADA",
                        tipo, icono, idZona, x, y,
                        a.getColaVirtual() != null ? a.getColaVirtual().tamanio() : 0,
                        a.getMinutosEsperaEstimados()));
            }
        }

        // Construir aristas (no dirigidas: emitir cada par solo una vez)
        for (ListaEnlazada.Iterador<Atraccion> it = atracciones.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            ListaEnlazada<String> vecinos = contexto.getGrafoDelParque().obtenerVecinos(a.getId());
            for (ListaEnlazada.Iterador<String> ni = vecinos.iterador(); ni.tieneSiguiente(); ) {
                String idVecino = ni.siguiente();
                if (a.getId().compareTo(idVecino) < 0) {
                    int peso = contexto.getGrafoDelParque().distanciaDijkstra(a.getId(), idVecino);
                    aristas.agregarAlFinal(new DatosArista(
                            a.getId(), idVecino, peso == Integer.MAX_VALUE ? 0 : peso));
                }
            }
        }

        req.setAttribute("nodes", nodos);
        req.setAttribute("edges", aristas);
        if (camino != null)        req.setAttribute("route", camino);
        if (distanciaRuta != null) req.setAttribute("routeDistance", distanciaRuta);
    }

    // ------------------------------------------------------------------ Portadores de datos

    public static class DatosNodo {
        public final String id, nombre, estado, tipo, icono, idZona;
        public final int x, y, cola, espera;

        public DatosNodo(String id, String nombre, String estado, String tipo, String icono,
                         String idZona, int x, int y, int cola, int espera) {
            this.id = id; this.nombre = nombre; this.estado = estado; this.tipo = tipo;
            this.icono = icono; this.idZona = idZona; this.x = x; this.y = y;
            this.cola = cola; this.espera = espera;
        }
    }

    public static class DatosArista {
        public final String desde, hasta;
        public final int peso;

        public DatosArista(String desde, String hasta, int peso) {
            this.desde = desde; this.hasta = hasta; this.peso = peso;
        }
    }
}
