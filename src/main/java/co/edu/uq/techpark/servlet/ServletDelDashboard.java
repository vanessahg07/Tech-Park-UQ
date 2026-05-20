package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Panel principal del administrador. */
public class ServletDelDashboard extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        // Pasar listas en orden del árbol directamente — sin colecciones de java.util
        // Leer mensajes flash de persistencia
        String flashMsg = (String) req.getSession().getAttribute("flashMessage");
        String flashErr = (String) req.getSession().getAttribute("flashError");
        if (flashMsg != null) { req.setAttribute("message", flashMsg); req.getSession().removeAttribute("flashMessage"); }
        if (flashErr != null) { req.setAttribute("error",   flashErr); req.getSession().removeAttribute("flashError"); }

        req.setAttribute("zones",       contexto.getZonas().enOrden());
        req.setAttribute("attractions", contexto.getAtraccionesPorId().enOrden());
        req.getRequestDispatcher("/WEB-INF/views/admin/panel.jsp").forward(req, resp);
    }
}
