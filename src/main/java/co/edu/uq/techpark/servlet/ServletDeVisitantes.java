package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.ds.ArbolBinarioBusqueda;
import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.model.RegistroDeVisita;
import co.edu.uq.techpark.service.ServicioDeVisitantes;
import co.edu.uq.techpark.util.ExcepcionDelParque;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Requisitos: 1.1, 1.2, 1.3, 1.4 */
public class ServletDeVisitantes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        if ("/profile".equals(ruta)) {
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            req.setAttribute("visitor", visitante);
            req.getRequestDispatcher("/WEB-INF/views/visitor/profile.jsp").forward(req, resp);

        } else if ("/register".equals(ruta)) {
            req.getRequestDispatcher("/WEB-INF/views/visitor/register.jsp").forward(req, resp);

        } else if ("/history".equals(ruta)) {
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            // Construir historial invertido como nueva ListaEnlazada
            ListaEnlazada<RegistroDeVisita> historial = visitante.getHistorialDeVisitas();
            // Invertir: construir nueva lista agregando al inicio
            ListaEnlazada<RegistroDeVisita> invertido = new ListaEnlazada<>();
            for (ListaEnlazada.Iterador<RegistroDeVisita> it = historial.iterador(); it.tieneSiguiente(); ) {
                invertido.agregarAlInicio(it.siguiente());
            }
            req.setAttribute("history", invertido);
            req.getRequestDispatcher("/WEB-INF/views/visitor/history.jsp").forward(req, resp);

        } else if ("/notifications".equals(ruta)) {
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            req.setAttribute("notifications", visitante.getNotificacionesSinLeer());
            req.getRequestDispatcher("/WEB-INF/views/visitor/notifications.jsp").forward(req, resp);

        } else if ("/favorites".equals(ruta)) {
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            ListaEnlazada<String> favoritos = visitante.getFavoritos().aListaEnlazada();
            req.setAttribute("favorites", favoritos);
            // Construir árbol de búsqueda para lookup en JSP
            ArbolBinarioBusqueda<String, Atraccion> mapaAtracciones = new ArbolBinarioBusqueda<>();
            ListaEnlazada<Atraccion> todasAtracciones = contexto.getAtraccionesPorId().enOrden();
            for (ListaEnlazada.Iterador<Atraccion> it = todasAtracciones.iterador(); it.tieneSiguiente(); ) {
                Atraccion a = it.siguiente();
                mapaAtracciones.insertar(a.getId(), a);
            }
            req.setAttribute("attractionMap", mapaAtracciones);
            req.getRequestDispatcher("/WEB-INF/views/visitor/favorites.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        ContextoDelParque contexto = ContextoDelParque.obtenerInstancia(getServletContext());
        if (contexto == null) contexto = ContextoDelParque.inicializar(getServletContext());

        if ("/register".equals(ruta)) {
            Visitante visitante = new Visitante();
            visitante.setNombreCompleto(req.getParameter("fullName"));
            visitante.setNumeroDocumento(req.getParameter("documentNumber"));
            visitante.setEdad(Integer.parseInt(req.getParameter("age")));
            visitante.setEstaturaCm(Integer.parseInt(req.getParameter("heightCm")));
            String saldoStr = req.getParameter("virtualBalance");
            visitante.setSaldoVirtual(saldoStr != null && !saldoStr.isEmpty()
                    ? Double.parseDouble(saldoStr) : 50000.0);

            String contrasena = req.getParameter("password");
            if (contrasena != null && !contrasena.isEmpty()) {
                visitante.setHashContrasena(Personal.hashContrasena(contrasena));
            }

            try {
                Part parteFoto = req.getPart("photo");
                if (parteFoto != null && parteFoto.getSize() > 0) {
                    try (InputStream is = parteFoto.getInputStream()) {
                        visitante.setFoto(leerBytes(is));
                    }
                }
            } catch (Exception ignorado) {}

            try {
                ServicioDeVisitantes.registrar(visitante, contexto);
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            } catch (ExcepcionDelParque e) {
                req.setAttribute("error", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/visitor/register.jsp").forward(req, resp);
            }

        } else if ("/update".equals(ruta)) {
            Visitante existente = (Visitante) req.getSession().getAttribute("user");
            if (existente == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            
            existente.setNombreCompleto(req.getParameter("fullName"));
            String edadStr    = req.getParameter("age");
            String estaturaStr = req.getParameter("heightCm");
            if (edadStr    != null && !edadStr.isEmpty())    existente.setEdad(Integer.parseInt(edadStr));
            if (estaturaStr != null && !estaturaStr.isEmpty()) existente.setEstaturaCm(Integer.parseInt(estaturaStr));

            try {
                Part parteFoto = req.getPart("photo");
                if (parteFoto != null && parteFoto.getSize() > 0) {
                    try (InputStream is = parteFoto.getInputStream()) {
                        existente.setFoto(leerBytes(is));
                    }
                }
            } catch (Exception ignorado) {}

            try {
                ServicioDeVisitantes.actualizarPerfil(existente, contexto);
                req.getSession().setAttribute("user", existente);
            } catch (ExcepcionDelParque e) {
                req.setAttribute("error", e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/visitor/profile");
            
        } else if ("/favorites/add".equals(ruta)) {
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            
            String idAtraccion = req.getParameter("attractionId");
            if (idAtraccion != null && !idAtraccion.isEmpty()) {
                boolean agregado = visitante.getFavoritos().agregar(idAtraccion);
                if (agregado) {
                    req.getSession().setAttribute("flashMessage", "Atracción agregada a favoritos");
                } else {
                    req.getSession().setAttribute("flashMessage", "Esta atracción ya está en tus favoritos");
                }
            }
            
            // Redirigir a la página de origen o a favoritos
            String referer = req.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                resp.sendRedirect(referer);
            } else {
                resp.sendRedirect(req.getContextPath() + "/visitor/favorites");
            }
            
        } else if ("/favorites/remove".equals(ruta)) {
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            
            String idAtraccion = req.getParameter("attractionId");
            if (idAtraccion != null && !idAtraccion.isEmpty()) {
                boolean eliminado = visitante.getFavoritos().eliminar(idAtraccion);
                if (eliminado) {
                    req.getSession().setAttribute("flashMessage", "Atracción eliminada de favoritos");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/visitor/favorites");

        } else if ("/recharge".equals(ruta)) {
            // El visitante recarga su propio saldo virtual
            Visitante visitante = (Visitante) req.getSession().getAttribute("user");
            if (visitante == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            try {
                double monto = Double.parseDouble(req.getParameter("amount"));
                if (monto <= 0) throw new ExcepcionDelParque("El monto debe ser mayor a cero");
                visitante.setSaldoVirtual(visitante.getSaldoVirtual() + monto);
                req.getSession().setAttribute("user", visitante);
                req.getSession().setAttribute("flashMessage",
                        "Recarga exitosa: $" + String.format("%,.2f", monto) + " agregados a tu saldo");
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("flashError", "Monto inválido");
            } catch (ExcepcionDelParque e) {
                req.getSession().setAttribute("flashError", e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/visitor/profile");
        }
    }

    /** Lee todos los bytes de un InputStream de forma compatible con Java 8. */
    private static byte[] leerBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int leidos;
        while ((leidos = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, leidos);
        }
        return buffer.toByteArray();
    }
}
