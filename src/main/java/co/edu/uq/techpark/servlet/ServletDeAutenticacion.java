package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.RolPersonal;
import co.edu.uq.techpark.service.ServicioDeAutenticacion;
import co.edu.uq.techpark.util.ExcepcionDeAutenticacion;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Requisitos: 12.1, 12.2, 12.3, 12.4, 12.5 */
public class ServletDeAutenticacion extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String accion = req.getParameter("action");

        if ("logout".equals(accion)) {
            HttpSession sesion = req.getSession(false);
            if (sesion != null) ServicioDeAutenticacion.cerrarSesion(sesion);
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String nombreUsuario = req.getParameter("username");
        String contrasena    = req.getParameter("password");

        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        try {
            Object autenticado = ServicioDeAutenticacion.iniciarSesion(nombreUsuario, contrasena, contexto);
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute("user", autenticado);

            if (autenticado instanceof Personal) {
                Personal miembro = (Personal) autenticado;
                if (miembro.getRol() == RolPersonal.ADMINISTRADOR) {
                    sesion.setAttribute("role", "ADMIN");
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                } else {
                    sesion.setAttribute("role", "OPERATOR");
                    resp.sendRedirect(req.getContextPath() + "/operator/panel");
                }
            } else {
                sesion.setAttribute("role", "VISITOR");
                resp.sendRedirect(req.getContextPath() + "/visitor/profile");
            }
        } catch (ExcepcionDeAutenticacion e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
