package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.TipoAlerta;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.service.ServicioDeAlertas;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 8.1, 8.2, 8.3 */
public class ServletDeAlertas extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        req.setAttribute("alerts", contexto.getAlertasActivas());
        req.getRequestDispatcher("/WEB-INF/views/admin/alerts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        try {
            if ("/register".equals(ruta)) {
                TipoAlerta tipo = TipoAlerta.valueOf(req.getParameter("type"));
                ServicioDeAlertas.registrarAlerta(tipo, contexto);
            } else if ("/cancel".equals(ruta)) {
                String idAlerta = req.getParameter("id");
                ServicioDeAlertas.cancelarAlerta(idAlerta, contexto);
            }
            resp.sendRedirect(req.getContextPath() + "/alert/list");
        } catch (ExcepcionDelParque e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("alerts", contexto.getAlertasActivas());
            req.getRequestDispatcher("/WEB-INF/views/admin/alerts.jsp").forward(req, resp);
        }
    }
}
