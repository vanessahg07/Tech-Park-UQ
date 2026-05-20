package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.*;
import co.edu.uq.techpark.service.ServicioDeAtracciones;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 4.1, 4.3, 4.4 */
public class ServletDeAtracciones extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        req.setAttribute("attractions", contexto.getAtraccionesPorId().enOrden());
        req.setAttribute("graph", contexto.getGrafoDelParque());
        req.getRequestDispatcher("/WEB-INF/views/admin/attractions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        try {
            if ("/add".equals(ruta)) {
                Atraccion a = new Atraccion();
                a.setNombre(req.getParameter("name"));
                a.setTipo(TipoAtraccion.valueOf(req.getParameter("type")));
                a.setCapacidadMaximaPorCiclo(Integer.parseInt(req.getParameter("maxCapacityPerCycle")));
                a.setEstaturaMinimaEnCm(Integer.parseInt(req.getParameter("minHeightCm")));
                a.setEdadMinima(Integer.parseInt(req.getParameter("minAge")));
                a.setCostoAdicional(Double.parseDouble(req.getParameter("additionalCost")));
                a.setIdZona(req.getParameter("zoneId"));
                ServicioDeAtracciones.agregar(a, contexto);
            } else if ("/update".equals(ruta)) {
                Atraccion a = new Atraccion();
                a.setId(req.getParameter("id"));
                a.setNombre(req.getParameter("name"));
                a.setTipo(TipoAtraccion.valueOf(req.getParameter("type")));
                a.setCapacidadMaximaPorCiclo(Integer.parseInt(req.getParameter("maxCapacityPerCycle")));
                a.setEstaturaMinimaEnCm(Integer.parseInt(req.getParameter("minHeightCm")));
                a.setEdadMinima(Integer.parseInt(req.getParameter("minAge")));
                a.setCostoAdicional(Double.parseDouble(req.getParameter("additionalCost")));
                a.setIdZona(req.getParameter("zoneId"));
                ServicioDeAtracciones.actualizar(a, contexto);
            } else if ("/status".equals(ruta)) {
                String id = req.getParameter("id");
                if (id == null || id.isEmpty()) id = req.getParameter("attractionId");
                EstadoAtraccion estado = EstadoAtraccion.valueOf(req.getParameter("status"));
                String motivo = req.getParameter("reason");
                ServicioDeAtracciones.cambiarEstado(id, estado, motivo, contexto);
                // Redirigir al panel del operador si fue llamado desde allí
                String referer = req.getHeader("Referer");
                if (referer != null && referer.contains("/operator/")) {
                    resp.sendRedirect(req.getContextPath() + "/operator/panel");
                    return;
                }
            } else if ("/edge/add".equals(ruta)) {
                String idDesde = req.getParameter("fromId");
                String idHasta = req.getParameter("toId");
                int peso       = Integer.parseInt(req.getParameter("weight"));
                contexto.getGrafoDelParque().agregarArista(idDesde, idHasta, peso);
            } else if ("/edge/remove".equals(ruta)) {
                String idDesde = req.getParameter("fromId");
                String idHasta = req.getParameter("toId");
                contexto.getGrafoDelParque().eliminarArista(idDesde, idHasta);
            }
            resp.sendRedirect(req.getContextPath() + "/attraction/list");
        } catch (ExcepcionDelParque e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("attractions", contexto.getAtraccionesPorId().enOrden());
            req.getRequestDispatcher("/WEB-INF/views/admin/attractions.jsp").forward(req, resp);
        }
    }
}
