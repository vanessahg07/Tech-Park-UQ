package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.service.ServicioDeEscenario;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/** Requisitos: 13.1, 13.2, 13.3, 13.4 */
@MultipartConfig
public class ServletDeEscenario extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/admin/scenario.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        try {
            Part partArchivo = req.getPart("scenarioFile");

            // Tomcat 7 (Servlet 3.0) no tiene getSubmittedFileName() — usar getHeader
            String nombreArchivo = "";
            String contentDisposition = partArchivo.getHeader("content-disposition");
            if (contentDisposition != null) {
                for (String parte : contentDisposition.split(";")) {
                    parte = parte.trim();
                    if (parte.startsWith("filename")) {
                        nombreArchivo = parte.substring(parte.indexOf('=') + 1).trim().replace("\"", "");
                    }
                }
            }

            // Rechazar archivos .dat y .ser — esos son de persistencia, no de escenario
            if (nombreArchivo.endsWith(".dat") || nombreArchivo.endsWith(".ser")) {
                req.setAttribute("error",
                    "El archivo '" + nombreArchivo + "' es un archivo de estado serializado. " +
                    "Para restaurarlo usa el botón 'Cargar' en el Panel de Administración. " +
                    "Esta sección solo acepta archivos de texto con formato [ZONES], [ATTRACTIONS], etc.");
                req.getRequestDispatcher(VISTA).forward(req, resp);
                return;
            }

            try (InputStream entrada = partArchivo.getInputStream()) {
                ServicioDeEscenario.ResultadoDeEscenario resultado = ServicioDeEscenario.cargar(entrada, contexto);
                req.setAttribute("scenarioResult", resultado);
                req.setAttribute("summary", "Escenario cargado: " + resultado.zonas + " zonas, "
                        + resultado.atracciones + " atracciones, " + resultado.personal + " personal, "
                        + resultado.visitantes + " visitantes.");
            }
        } catch (ExcepcionDelParque e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }
}
