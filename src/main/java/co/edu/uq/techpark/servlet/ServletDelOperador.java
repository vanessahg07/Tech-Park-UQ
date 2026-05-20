package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.ds.ArbolBinarioBusqueda;
import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.Zona;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Panel del operador: muestra las atracciones de su zona asignada. */
public class ServletDelOperador extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        Personal operador = (Personal) req.getSession().getAttribute("user");
        Zona zona = null;
        ListaEnlazada<Atraccion> atracciones = new ListaEnlazada<>();
        // ArbolBinarioBusqueda<idAtraccion, Integer> para tamaños de cola
        ArbolBinarioBusqueda<String, Integer> tamanosCola = new ArbolBinarioBusqueda<>();

        if (operador != null && operador.getIdZonaAsignada() != null) {
            zona = contexto.getZonas().buscar(operador.getIdZonaAsignada());
            if (zona != null) {
                for (ListaEnlazada.Iterador<String> it = zona.getIdsDeAtracciones().iterador(); it.tieneSiguiente(); ) {
                    String idAtraccion = it.siguiente();
                    Atraccion a = contexto.getAtraccionesPorId().buscar(idAtraccion);
                    if (a != null) {
                        atracciones.agregarAlFinal(a);
                        tamanosCola.insertar(idAtraccion, a.getColaVirtual() != null ? a.getColaVirtual().tamanio() : 0);
                    }
                }
            }
        }

        req.setAttribute("zone", zona);
        req.setAttribute("attractions", atracciones);
        req.setAttribute("queueSizes", tamanosCola);
        req.getRequestDispatcher("/WEB-INF/views/operator/panel.jsp").forward(req, resp);
    }
}
