<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Notificacion" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    ListaEnlazada<Notificacion> notificaciones = (ListaEnlazada<Notificacion>) request.getAttribute("notifications");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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
    <title>Notificaciones — Tech-Park UQ</title>
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
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/favorites">Favoritos</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/visitor/notifications">Notificaciones
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
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Notificaciones</h2>
        <% if (notificaciones != null && !notificaciones.estaVacia()) { %>
        <form method="post" action="${pageContext.request.contextPath}/visitor/notifications/read">
            <input type="hidden" name="markAll" value="true">
            <button type="submit" class="btn btn-outline-secondary btn-sm">✔ Marcar todas como leídas</button>
        </form>
        <% } %>
    </div>
    <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
    <% if (success != null) { %><div class="alert alert-success"><%= success %></div><% } %>
    <% if (notificaciones == null || notificaciones.estaVacia()) { %>
    <div class="alert alert-info">No tienes notificaciones pendientes.</div>
    <% } else { %>
    <div class="list-group">
        <% for (ListaEnlazada.Iterador<Notificacion> _nit = notificaciones.iterador(); _nit.tieneSiguiente(); ) {
               Notificacion notif = _nit.siguiente();
               String msg = notif.getMensaje() != null ? notif.getMensaje() : "";
               String itemClass = "list-group-item-light";
               if (msg.startsWith("🔔"))      itemClass = "list-group-item-success";
               else if (msg.startsWith("🎢")) itemClass = "list-group-item-info";
               else if (msg.startsWith("🚫")) itemClass = "list-group-item-danger";
               else if (msg.startsWith("🔧")) itemClass = "list-group-item-warning";
               else if (msg.startsWith("⛈️")) itemClass = "list-group-item-danger";
        %>
        <div class="list-group-item list-group-item-action <%= itemClass %> d-flex justify-content-between align-items-start gap-3">
            <div class="flex-grow-1">
                <div class="d-flex justify-content-between">
                    <p class="mb-1"><%= msg %></p>
                    <small class="text-muted text-nowrap ms-3"><%= notif.getCreadaEn() != null ? notif.getCreadaEn().format(fmt) : "—" %></small>
                </div>
                <% if (!notif.isLeida()) { %><span class="badge bg-primary">Nueva</span><% } %>
            </div>
            <% if (!notif.isLeida()) { %>
            <form method="post" action="${pageContext.request.contextPath}/visitor/notifications/read" class="flex-shrink-0">
                <input type="hidden" name="notificationId" value="<%= notif.getId() %>">
                <button type="submit" class="btn btn-outline-primary btn-sm">✔ Leída</button>
            </form>
            <% } %>
        </div>
        <% } %>
    </div>
    <div class="mt-3 text-muted small"><%= notificaciones.tamanio() %> notificación(es) no leída(s)</div>
    <% } %>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
