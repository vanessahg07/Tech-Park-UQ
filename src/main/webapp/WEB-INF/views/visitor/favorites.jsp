<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Atraccion" %>
<%@ page import="co.edu.uq.techpark.model.EstadoAtraccion" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%@ page import="co.edu.uq.techpark.ds.ArbolBinarioBusqueda" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    ListaEnlazada<String> favoritos = (ListaEnlazada<String>) request.getAttribute("favorites");
    ArbolBinarioBusqueda<String, Atraccion> mapaAtracciones = (ArbolBinarioBusqueda<String, Atraccion>) request.getAttribute("attractionMap");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    Visitante visitanteNav = (Visitante) session.getAttribute("user");
    int notifCount = (visitanteNav != null && visitanteNav.getNotificacionesSinLeer() != null)
                     ? visitanteNav.getNotificacionesSinLeer().tamanio() : 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Favoritos — Tech-Park UQ</title>
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
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/history">Historial</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/visitor/favorites">Favoritos</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/notifications">Notificaciones
                    <% if (notifCount > 0) { %><span class="badge bg-danger rounded-pill"><%= notifCount %></span><% } %>
                </a></li>
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
    <h2 class="mb-4">⭐ Mis Favoritos</h2>

    <% if (error != null) { %>
    <div class="alert alert-danger alert-dismissible fade show">
        <%= error %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>
    <% if (success != null) { %>
    <div class="alert alert-success alert-dismissible fade show">
        <%= success %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>
    
    <% 
    String flashMsg = (String) session.getAttribute("flashMessage");
    if (flashMsg != null) { 
        session.removeAttribute("flashMessage");
    %>
    <div class="alert alert-success alert-dismissible fade show">
        <%= flashMsg %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <% if (favoritos == null || favoritos.estaVacia()) { %>
    <div class="alert alert-info">No tienes atracciones favoritas guardadas.</div>
    <% } else { %>
    <div class="row g-3">
        <% for (ListaEnlazada.Iterador<String> _fit = favoritos.iterador(); _fit.tieneSiguiente(); ) {
            String favId = _fit.siguiente();
            Atraccion attr = mapaAtracciones != null ? mapaAtracciones.buscar(favId) : null;
            String nombreMostrado = attr != null ? attr.getNombre() : favId;
            String badgeEstado = "";
            if (attr != null && attr.getEstado() != null) {
                String estadoNombre = attr.getEstado().name();
                if ("ACTIVA".equals(estadoNombre)) {
                    badgeEstado = "<span class=\"badge bg-success\">Activa</span>";
                } else if ("EN_MANTENIMIENTO".equals(estadoNombre)) {
                    badgeEstado = "<span class=\"badge bg-warning text-dark\">Mantenimiento</span>";
                } else {
                    badgeEstado = "<span class=\"badge bg-danger\">Cerrada</span>";
                }
            }
        %>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100">
                <div class="card-body d-flex justify-content-between align-items-start">
                    <div>
                        <h6 class="card-title mb-1">⭐ <%= nombreMostrado %></h6>
                        <% if (!badgeEstado.isEmpty()) { %><%= badgeEstado %><% } %>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/visitor/favorites/remove" class="ms-2">
                        <input type="hidden" name="attractionId" value="<%= favId %>">
                        <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('¿Eliminar de favoritos?')">✖</button>
                    </form>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <div class="mt-3 text-muted small">Total: <%= favoritos.tamanio() %> favorito(s)</div>
    <% } %>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
