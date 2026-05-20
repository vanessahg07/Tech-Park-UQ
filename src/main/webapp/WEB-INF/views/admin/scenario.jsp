<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
    String resumen = (String) request.getAttribute("summary");
    String error   = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Escenario de Prueba</title>
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
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/attraction/list">Atracciones</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/staff/list">Personal</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/alert/list">Alertas</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/report">Reporte</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/scenario">Escenario</a></li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><span class="nav-link text-white-50"><%= usuarioActual != null ? usuarioActual.getNombreCompleto() : "" %> (ADMIN)</span></li>
                <li class="nav-item"><form method="post" action="${pageContext.request.contextPath}/auth" class="d-inline"><input type="hidden" name="action" value="logout"><button type="submit" class="btn btn-outline-light btn-sm ms-2">Cerrar sesión</button></form></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container py-4">
    <h2 class="mb-1">Carga de Escenario de Prueba</h2>
    <p class="text-muted mb-4">Inicializa el sistema con datos representativos desde un archivo</p>
    <div class="row justify-content-center">
        <div class="col-lg-7">
            <% if (error != null) { %><div class="alert alert-danger alert-dismissible fade show"><strong>Error:</strong><br><pre class="mb-0 mt-1" style="white-space:pre-wrap;font-size:.875rem;"><%= error %></pre><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
            <% if (resumen != null) { %><div class="alert alert-success alert-dismissible fade show"><strong>✅ Escenario cargado exitosamente</strong><hr><pre class="mb-0" style="white-space:pre-wrap;font-size:.875rem;"><%= resumen %></pre><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
            <div class="card shadow-sm">
                <div class="card-header bg-secondary text-white"><h6 class="mb-0">📂 Cargar Archivo de Escenario</h6></div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/scenario" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="scenarioFile" class="form-label">Archivo de escenario <span class="text-danger">*</span></label>
                            <input type="file" id="scenarioFile" name="scenarioFile" class="form-control" accept=".txt,.json,.dat,.ser" required>
                            <div class="form-text">Formatos: <code>.txt</code>, <code>.json</code>, <code>.dat</code>, <code>.ser</code></div>
                        </div>
                        <div class="alert alert-warning py-2 small mb-3">
                            <strong>⚠️ Advertencia:</strong> Cargar un escenario reemplazará el estado actual del sistema.
                        </div>
                        <div class="alert alert-info py-2 small mb-3">
                            <strong>ℹ️ ¿Tienes un archivo <code>.dat</code>?</strong>
                            Ese es un archivo de estado guardado. Usa el botón
                            <strong>"Cargar"</strong> en el
                            <a href="${pageContext.request.contextPath}/dashboard">Panel de Administración</a>,
                            sección <em>Guardar / Cargar Estado</em>.
                            Esta sección solo acepta archivos <code>.txt</code> de escenario.
                        </div>
                        <button type="submit" class="btn btn-secondary w-100">Cargar Escenario</button>
                    </form>
                </div>
            </div>
            <div class="card mt-3 shadow-sm">
                <div class="card-header"><h6 class="mb-0">ℹ️ Formato del archivo</h6></div>
                <div class="card-body small text-muted">
                    <pre class="bg-light p-2 rounded" style="font-size:.8rem;">[ZONES]
id,nombre,capacidadMax

[ATTRACTIONS]
id,nombre,tipo,capacidadCiclo,alturaMin,edadMin,costo,zonaId

[STAFF]
id,nombreCompleto,username,password,rol,zonaId

[VISITORS]
id,nombreCompleto,documento,edad,estatura,saldo</pre>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
