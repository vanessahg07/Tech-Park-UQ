<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Panel de Administración</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<nav class="navbar navbar-expand-lg">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/dashboard">🎢 Tech-Park UQ</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain"><span class="navbar-toggler-icon"></span></button>
        <div class="collapse navbar-collapse" id="navMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/attraction/list">Atracciones</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/staff/list">Personal</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/alert/list">Alertas</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/report">Reporte</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scenario">Escenario</a></li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><span class="nav-link text-white-50"><%= usuarioActual != null ? usuarioActual.getNombreCompleto() : "" %> (ADMIN)</span></li>
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
    <h2 class="mb-1">Panel de Administración</h2>
    <p class="text-muted mb-4">Gestión integral del parque</p>
    <% String msg = (String) request.getAttribute("message"); %>
    <% String err = (String) request.getAttribute("error"); %>
    <% if (msg != null) { %><div class="alert alert-success alert-dismissible fade show"><%= msg %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
    <% if (err != null) { %><div class="alert alert-danger alert-dismissible fade show"><%= err %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
    <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
        <div class="col"><a href="${pageContext.request.contextPath}/attraction/list" class="text-decoration-none">
            <div class="card h-100 border-primary"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">🎡</div><h5 class="card-title text-primary">Atracciones</h5>
                <p class="card-text text-muted small">Gestionar catálogo, estados y mantenimiento.</p>
            </div></div></a></div>
        <div class="col"><a href="${pageContext.request.contextPath}/staff/list" class="text-decoration-none">
            <div class="card h-100 border-success"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">👷</div><h5 class="card-title text-success">Personal</h5>
                <p class="card-text text-muted small">Crear, editar y asignar operadores a zonas.</p>
            </div></div></a></div>
        <div class="col"><a href="${pageContext.request.contextPath}/admin/visitors" class="text-decoration-none">
            <div class="card h-100 border-success"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">👥</div><h5 class="card-title text-success">Visitantes</h5>
                <p class="card-text text-muted small">Consultar visitantes y recargar saldo virtual.</p>
            </div></div></a></div>
        <div class="col"><a href="${pageContext.request.contextPath}/alert/list" class="text-decoration-none">
            <div class="card h-100 border-warning"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">⚠️</div><h5 class="card-title text-warning">Alertas Climáticas</h5>
                <p class="card-text text-muted small">Registrar y cancelar alertas climáticas activas.</p>
            </div></div></a></div>
        <div class="col"><a href="${pageContext.request.contextPath}/report" class="text-decoration-none">
            <div class="card h-100 border-info"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">📊</div><h5 class="card-title text-info">Reportes</h5>
                <p class="card-text text-muted small">Ver y exportar el Reporte de Jornada.</p>
            </div></div></a></div>
        <div class="col"><a href="${pageContext.request.contextPath}/scenario" class="text-decoration-none">
            <div class="card h-100 border-secondary"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">📂</div><h5 class="card-title text-secondary">Escenario de Prueba</h5>
                <p class="card-text text-muted small">Cargar un archivo de escenario para inicializar el sistema.</p>
            </div></div></a></div>
        <div class="col">
            <div class="card h-100 border-dark"><div class="card-body text-center py-4">
                <div class="display-4 mb-2">💾</div><h5 class="card-title text-dark">Guardar / Cargar Estado</h5>
                <p class="card-text text-muted small">Persistir o restaurar el estado completo del parque.</p>
                <div class="d-flex gap-2 justify-content-center mt-3">
                    <form method="post" action="${pageContext.request.contextPath}/persistence/save"><button type="submit" class="btn btn-dark btn-sm">Guardar</button></form>
                    <form method="post" action="${pageContext.request.contextPath}/persistence/load"><button type="submit" class="btn btn-outline-dark btn-sm">Cargar</button></form>
                </div>
            </div></div></div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
