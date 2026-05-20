package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.service.ServicioDeReportes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** Requisitos: 11.1, 11.3 */
public class ServletDeReportes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        ServicioDeReportes.ReporteDeJornada reporte = ServicioDeReportes.generarReporteDeJornada(contexto);
        req.setAttribute("report", reporte);
        req.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        ServicioDeReportes.ReporteDeJornada reporte = ServicioDeReportes.generarReporteDeJornada(contexto);
        String texto = ServicioDeReportes.exportarComoTexto(reporte);
        resp.setContentType("text/plain; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=reporte_jornada.txt");
        try (PrintWriter escritor = resp.getWriter()) {
            escritor.write(texto);
        }
    }
}
