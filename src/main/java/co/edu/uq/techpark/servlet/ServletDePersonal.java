package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.RolPersonal;
import co.edu.uq.techpark.model.Zona;
import co.edu.uq.techpark.service.ServicioDePersonal;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Requisitos: 9.1, 9.2, 9.3 */
public class ServletDePersonal extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());
        req.setAttribute("staffList", contexto.getPersonal().enOrden());
        req.setAttribute("zones",     contexto.getZonas().enOrden());
        req.getRequestDispatcher("/WEB-INF/views/admin/staff.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        try {
            if ("/create".equals(ruta)) {
                Personal miembro = new Personal();
                miembro.setNombreUsuario(req.getParameter("username"));
                miembro.setHashContrasena(Personal.hashContrasena(req.getParameter("password")));
                miembro.setNombreCompleto(req.getParameter("fullName"));
                miembro.setRol(RolPersonal.valueOf(req.getParameter("role")));
                ServicioDePersonal.crear(miembro, contexto);
                // Asignar zona si se proporcionó
                String idZonaCrear = req.getParameter("zoneId");
                if (idZonaCrear != null && !idZonaCrear.isEmpty()) {
                    Zona zonaCrear = contexto.getZonas().buscar(idZonaCrear);
                    if (zonaCrear != null) ServicioDePersonal.asignarAZona(miembro, zonaCrear, contexto);
                }
            } else if ("/deactivate".equals(ruta)) {
                // El form envía el ID (UUID) del personal; hay que buscar por ID para obtener el nombreUsuario
                String idPersonalDesactivar = req.getParameter("id");
                Personal miembroDesactivar = null;
                for (ListaEnlazada.Iterador<Personal> it = contexto.getPersonal().enOrden().iterador(); it.tieneSiguiente(); ) {
                    Personal p = it.siguiente();
                    if (p.getId().equals(idPersonalDesactivar)) { miembroDesactivar = p; break; }
                }
                if (miembroDesactivar == null) throw new ExcepcionDelParque("Personal no encontrado con id: " + idPersonalDesactivar);
                ServicioDePersonal.desactivar(miembroDesactivar.getNombreUsuario(), contexto);
            } else if ("/assign".equals(ruta)) {
                // El form envía staffId (ID del personal) y zoneId
                String idPersonal = req.getParameter("staffId");
                String idZona     = req.getParameter("zoneId");
                // Buscar personal por ID recorriendo el árbol
                Personal miembro = null;
                for (ListaEnlazada.Iterador<Personal> it = contexto.getPersonal().enOrden().iterador(); it.tieneSiguiente(); ) {
                    Personal p = it.siguiente();
                    if (p.getId().equals(idPersonal)) { miembro = p; break; }
                }
                Zona zona = contexto.getZonas().buscar(idZona);
                if (miembro != null && zona != null) ServicioDePersonal.asignarAZona(miembro, zona, contexto);
            }
            resp.sendRedirect(req.getContextPath() + "/staff/list");
        } catch (ExcepcionDelParque e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("staffList", contexto.getPersonal().enOrden());
            req.setAttribute("zones",     contexto.getZonas().enOrden());
            req.getRequestDispatcher("/WEB-INF/views/admin/staff.jsp").forward(req, resp);
        }
    }
}
