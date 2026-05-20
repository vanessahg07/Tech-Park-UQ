<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%@ page import="co.edu.uq.techpark.model.Atraccion" %>
<%@ page import="co.edu.uq.techpark.service.ServicioDeReportes" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
    ServicioDeReportes.ReporteDeJornada reporte = (ServicioDeReportes.ReporteDeJornada) request.getAttribute("report");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Reporte de Jornada</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<nav class="navbar navbar-expand-lg">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">🎢 Tech-Park UQ</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain"><span class="navbar-toggler-icon"></span></button>
        <div class="collapse navbar-collapse" id="navMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/attraction/list">Atracciones</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/staff/list">Personal</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/alert/list">Alertas</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/report">Reporte</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scenario">Escenario</a></li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><span class="nav-link text-white-50"><%= usuarioActual != null ? usuarioActual.getNombreCompleto() : "" %> (ADMIN)</span></li>
                <li class="nav-item"><form method="post" action="${pageContext.request.contextPath}/auth" class="d-inline"><input type="hidden" name="action" value="logout"><button type="submit" class="btn btn-outline-light btn-sm ms-2">Cerrar sesión</button></form></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-end mb-4">
        <div><h2 class="mb-1">Reporte de Jornada</h2><p class="mb-0 text-muted">Estadísticas operativas del parque</p></div>
        <form method="post" action="${pageContext.request.contextPath}/report">
            <input type="hidden" name="action" value="export">
            <button type="submit" class="btn btn-outline-dark">📄 Exportar como texto</button>
        </form>
    </div>
    <% if (error != null) { %><div class="alert alert-danger"><%= error %></div><% } %>
    <% if (reporte == null) { %>
    <div class="alert alert-warning">No hay datos de reporte disponibles aún.</div>
    <% } else { %>
    <div class="row g-3 mb-4">
        <div class="col-6 col-md-3"><div class="card text-center p-3"><div class="fs-4 fw-bold">$<%= String.format("%,.0f", reporte.ingresosTotales) %></div><div class="text-muted small">Ingresos Totales</div></div></div>
        <div class="col-6 col-md-3"><div class="card text-center p-3"><div class="fs-4 fw-bold"><%= reporte.cierresPorAlertaClimatica %></div><div class="text-muted small">Cierres por Alerta</div></div></div>
        <div class="col-6 col-md-3"><div class="card text-center p-3"><div class="fs-4 fw-bold"><%= reporte.atraccionesEnMantenimiento.tamanio() %></div><div class="text-muted small">En Mantenimiento</div></div></div>
        <div class="col-6 col-md-3"><div class="card text-center p-3"><div class="fs-4 fw-bold"><%= String.format("%.1f", reporte.minutosEsperaPromedio) %></div><div class="text-muted small">Espera Prom. (min)</div></div></div>
    </div>
    <div class="row g-4">
        <div class="col-lg-7">
            <div class="card">
                <div class="card-header bg-primary"><h6 class="mb-0 text-white">🏆 Atracciones por Visitantes</h6></div>
                <div class="card-body p-0">
                    <table class="table table-hover mb-0">
                        <thead><tr><th>#</th><th>Atracción</th><th>Tipo</th><th>Visitantes</th><th>Estado</th></tr></thead>
                        <tbody>
                        <% if (reporte.atraccionesPorVisitantes == null || reporte.atraccionesPorVisitantes.estaVacia()) { %>
                            <tr><td colspan="5" class="text-center text-muted py-3">Sin datos registrados.</td></tr>
                        <% } else { int rango = 1;
                            for (ListaEnlazada.Iterador<Atraccion> _it = reporte.atraccionesPorVisitantes.iterador(); _it.tieneSiguiente(); ) {
                                Atraccion a = _it.siguiente(); %>
                        <tr>
                            <td><% if (rango==1) { %>🥇<% } else if (rango==2) { %>🥈<% } else if (rango==3) { %>🥉<% } else { %><span class="badge bg-secondary"><%= rango %></span><% } rango++; %></td>
                            <td class="fw-semibold"><%= a.getNombre() %></td>
                            <td><small class="text-muted"><%= a.getTipo() != null ? a.getTipo().name() : "—" %></small></td>
                            <td><strong><%= a.getVisitantesAcumulados() %></strong></td>
                            <td><span class="badge badge-<%= a.getEstado() != null ? a.getEstado().name() : "CERRADA" %>"><%= a.getEstado() != null ? a.getEstado().name() : "?" %></span></td>
                        </tr>
                        <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-lg-5">
            <div class="card mb-4">
                <div class="card-header bg-warning"><h6 class="mb-0 text-white">⚙️ En Mantenimiento</h6></div>
                <div class="card-body p-0">
                    <% if (reporte.atraccionesEnMantenimiento.estaVacia()) { %>
                    <div class="text-center py-3 text-muted small">✅ Ninguna en mantenimiento</div>
                    <% } else { %>
                    <table class="table table-hover mb-0">
                        <thead><tr><th>Atracción</th><th>Visitantes</th><th>Motivo</th></tr></thead>
                        <tbody>
                        <% for (ListaEnlazada.Iterador<Atraccion> _it2 = reporte.atraccionesEnMantenimiento.iterador(); _it2.tieneSiguiente(); ) {
                               Atraccion am = _it2.siguiente(); %>
                        <tr>
                            <td class="fw-semibold small"><%= am.getNombre() %></td>
                            <td><%= am.getVisitantesAcumulados() %></td>
                            <td><small class="text-muted"><%= am.getMotivoCierre() != null ? am.getMotivoCierre() : "Preventivo" %></small></td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
    <% } %>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
