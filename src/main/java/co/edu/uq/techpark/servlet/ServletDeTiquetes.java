package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.TipoTiquete;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.service.ServicioDeTiquetes;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 2.1, 2.5 */
public class ServletDeTiquetes extends HttpServlet {

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
        
        if ("/purchase".equals(ruta)) {
            req.setAttribute("visitor", visitante);
            req.getRequestDispatcher("/WEB-INF/views/visitor/tickets.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/ticket/purchase");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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

        if ("/purchase".equals(ruta)) {
            TipoTiquete tipo = TipoTiquete.valueOf(req.getParameter("ticketType"));
            try {
                ServicioDeTiquetes.comprar(visitante, tipo, contexto);
                req.getSession().setAttribute("user", visitante); // refrescar sesión
                resp.sendRedirect(req.getContextPath() + "/visitor/profile");
            } catch (ExcepcionDelParque e) {
                req.setAttribute("error", e.getMessage());
                req.setAttribute("visitor", visitante);
                req.getRequestDispatcher("/WEB-INF/views/visitor/tickets.jsp").forward(req, resp);
            }
        }
    }
}
