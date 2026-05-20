package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.ds.ArbolBinarioBusqueda;
import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.service.ServicioDeAtracciones;
import co.edu.uq.techpark.service.ServicioDeCola;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 3.1, 3.2, 3.3, 3.4, 5.1, 5.2, 5.3, 5.4, 5.5 */
public class ServletDeCola extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        
        // Validar sesión
        Visitante visitante = (Visitante) req.getSession().getAttribute("user");
        if (visitante == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        if ("/status".equals(ruta)) {
            ListaEnlazada<Atraccion> atracciones = contexto.getAtraccionesPorId().enOrden();
            // ArbolBinarioBusqueda<idAtraccion, posicion> para las posiciones en cola de este visitante
            ArbolBinarioBusqueda<String, Integer> estadosCola = new ArbolBinarioBusqueda<>();
            for (ListaEnlazada.Iterador<Atraccion> it = atracciones.iterador(); it.tieneSiguiente(); ) {
                Atraccion a = it.siguiente();
                int pos = ServicioDeCola.obtenerPosicion(visitante, a);
                if (pos > 0) estadosCola.insertar(a.getId(), pos);
            }
            req.setAttribute("attractions", atracciones);
            req.setAttribute("queueStatuses", estadosCola);
            req.getRequestDispatcher("/WEB-INF/views/visitor/queue.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/queue/status");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        String idAtraccion = req.getParameter("attractionId");
        Atraccion atraccion = ServicioDeAtracciones.buscarPorId(idAtraccion, contexto);

        Object usuarioObj = req.getSession().getAttribute("user");
        Visitante visitante = (usuarioObj instanceof Visitante) ? (Visitante) usuarioObj : null;

        try {
            if ("/join".equals(ruta)) {
                if (visitante == null) {
                    resp.sendRedirect(req.getContextPath() + "/login.jsp");
                    return;
                }
                ServicioDeCola.encolar(visitante, atraccion, contexto);
            } else if ("/cancel".equals(ruta)) {
                if (visitante == null) {
                    resp.sendRedirect(req.getContextPath() + "/login.jsp");
                    return;
                }
                ServicioDeCola.cancelar(visitante, atraccion);
            } else if ("/cycle".equals(ruta)) {
                ServicioDeCola.desencolar(atraccion, contexto);
                resp.sendRedirect(req.getContextPath() + "/operator/panel");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/queue/status");
        } catch (ExcepcionDelParque e) {
            req.setAttribute("error", e.getMessage());
            // Recargar atracciones y estados de cola para que la vista no lance NPE
            ListaEnlazada<Atraccion> atracciones = contexto.getAtraccionesPorId().enOrden();
            ArbolBinarioBusqueda<String, Integer> estadosCola = new ArbolBinarioBusqueda<>();
            Visitante visitanteRecarga = (Visitante) req.getSession().getAttribute("user");
            if (visitanteRecarga != null) {
                for (ListaEnlazada.Iterador<Atraccion> it = atracciones.iterador(); it.tieneSiguiente(); ) {
                    Atraccion a = it.siguiente();
                    int pos = ServicioDeCola.obtenerPosicion(visitanteRecarga, a);
                    if (pos > 0) estadosCola.insertar(a.getId(), pos);
                }
            }
            req.setAttribute("attractions", atracciones);
            req.setAttribute("queueStatuses", estadosCola);
            req.getRequestDispatcher("/WEB-INF/views/visitor/queue.jsp").forward(req, resp);
        }
    }
}
