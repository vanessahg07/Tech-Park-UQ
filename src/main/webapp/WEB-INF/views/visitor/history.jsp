<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.RegistroDeVisita" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    ListaEnlazada<RegistroDeVisita> historial = (ListaEnlazada<RegistroDeVisita>) request.getAttribute("history");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Historial de Visitas — Tech-Park UQ</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<nav class="navbar navbar-expand-lg ">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/dashboard">🎢 Tech-Park UQ</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/profile">Mi Perfil</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/ticket/purchase">Tickets</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/queue/status">Mi Cola</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/map">Mapa</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/visitor/history">Historial</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/favorites">Favoritos</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/notifications">Notificaciones</a></li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <form method="post" action="${pageContext.request.contextPath}/auth" class="d-inline">
                        <input type="hidden" name="action" value="logout">
                        <button type="submit" class="btn btn-outline-light btn-sm ms-2">Cerrar sesión</button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-4">
    <h2 class="mb-4">Historial de visitas</h2>

    <% if (historial == null || historial.estaVacia()) { %>
    <div class="alert alert-info">Aún no has visitado ninguna atracción.</div>
    <% } else { %>
    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover mb-0">
                <thead class="table-light"><tr><th>#</th><th>Atracción</th><th>Fecha y hora</th></tr></thead>
                <tbody>
                    <% int idx = historial.tamanio();
                       for (ListaEnlazada.Iterador<RegistroDeVisita> _hit = historial.iterador(); _hit.tieneSiguiente(); ) {
                           RegistroDeVisita registro = _hit.siguiente(); %>
                    <tr>
                        <td class="text-muted small"><%= idx-- %></td>
                        <td><%= registro.getNombreAtraccion() != null ? registro.getNombreAtraccion() : registro.getIdAtraccion() %></td>
                        <td class="text-muted small"><%= registro.getVisitadaEn() != null ? registro.getVisitadaEn().format(fmt) : "—" %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <div class="card-footer text-muted small">Total de visitas: <%= historial.tamanio() %></div>
    </div>
    <% } %>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
