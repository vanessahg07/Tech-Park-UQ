package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.service.ServicioDePersistencia;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 16.1, 16.2 */
public class ServletDePersistencia extends HttpServlet {

    private static final String ARCHIVO_ESTADO = "parkstate.dat";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        try {
            if ("/save".equals(ruta)) {
                ServicioDePersistencia.guardar(contexto, ARCHIVO_ESTADO);
                req.getSession().setAttribute("flashMessage", "Estado guardado correctamente.");
            } else if ("/load".equals(ruta)) {
                ContextoDelParque cargado = ServicioDePersistencia.cargar(ARCHIVO_ESTADO);
                if (cargado != null) {
                    getServletContext().setAttribute(ContextoDelParque.CLAVE_CONTEXTO, cargado);
                    req.getSession().setAttribute("flashMessage", "Estado cargado correctamente.");
                } else {
                    req.getSession().setAttribute("flashMessage", "No se encontró archivo de estado. Se inició con estado vacío.");
                }
            }
        } catch (ExcepcionDelParque e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
