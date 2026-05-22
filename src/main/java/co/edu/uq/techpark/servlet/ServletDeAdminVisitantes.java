package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Gestión de visitantes desde el panel de administración.
 * Permite listar visitantes y recargar su saldo virtual.
 */
public class ServletDeAdminVisitantes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!"ADMIN".equals(req.getSession().getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        ListaEnlazada<Visitante> visitantes = contexto.getVisitantes().enOrden();
        req.setAttribute("visitantes", visitantes);

        String flashMessage = (String) req.getSession().getAttribute("flashMessage");
        String flashError   = (String) req.getSession().getAttribute("flashError");
        if (flashMessage != null) req.getSession().removeAttribute("flashMessage");
        if (flashError   != null) req.getSession().removeAttribute("flashError");
        req.setAttribute("flashMessage", flashMessage);
        req.setAttribute("flashError",   flashError);

        req.getRequestDispatcher("/WEB-INF/views/admin/visitors.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!"ADMIN".equals(req.getSession().getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        if ("/recharge".equals(ruta)) {
            String documento = req.getParameter("documento");
            try {
                double monto = Double.parseDouble(req.getParameter("amount"));
                if (monto <= 0) throw new ExcepcionDelParque("El monto debe ser mayor a cero");

                Visitante visitante = contexto.getVisitantes().buscar(documento);
                if (visitante == null) throw new ExcepcionDelParque("Visitante no encontrado: " + documento);

                visitante.setSaldoVirtual(visitante.getSaldoVirtual() + monto);
                req.getSession().setAttribute("flashMessage",
                        "Recarga exitosa: $" + String.format("%,.2f", monto)
                        + " agregados al saldo de " + visitante.getNombreCompleto());
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("flashError", "Monto inválido");
            } catch (ExcepcionDelParque e) {
                req.getSession().setAttribute("flashError", e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/visitors");
    }
}
