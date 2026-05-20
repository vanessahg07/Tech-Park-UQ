<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%@ page import="co.edu.uq.techpark.model.AlertaClimatica" %>
<%@ page import="co.edu.uq.techpark.model.TipoAlerta" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
    ListaEnlazada<AlertaClimatica> alertas = (ListaEnlazada<AlertaClimatica>) request.getAttribute("alerts");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Alertas Climáticas</title>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/alert/list">Alertas</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/report">Reporte</a></li>
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
    <h2 class="mb-1">Alertas Climáticas</h2>
    <p class="text-muted mb-4">Registra y gestiona alertas que afectan la operación del parque</p>
    <% if (error != null) { %><div class="alert alert-danger alert-dismissible fade show"><%= error %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
    <% if (success != null) { %><div class="alert alert-success alert-dismissible fade show"><%= success %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>

    <div class="row g-4">
        <div class="col-md-4">
            <div class="card shadow-sm border-warning">
                <div class="card-header bg-warning text-dark"><h6 class="mb-0">⚠️ Registrar Nueva Alerta</h6></div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/alert/register">
                        <div class="mb-3">
                            <label class="form-label">Tipo de Alerta <span class="text-danger">*</span></label>
                            <select name="type" class="form-select" required>
                                <option value="">— Seleccionar —</option>
                                <% for (TipoAlerta t : TipoAlerta.values()) { %><option value="<%= t.name() %>"><%= t.name().replace("_", " ") %></option><% } %>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-warning w-100 fw-semibold">Registrar Alerta</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-dark text-white"><h6 class="mb-0">Alertas Activas</h6></div>
                <div class="card-body p-0">
                    <% if (alertas == null || alertas.estaVacia()) { %>
                    <div class="text-center text-muted py-4"><div class="fs-1">✅</div><p class="mb-0">No hay alertas climáticas activas.</p></div>
                    <% } else { %>
                    <table class="table table-hover mb-0">
                        <thead class="table-light"><tr><th>Tipo</th><th>Inicio</th><th>Atracciones afectadas</th><th>Acción</th></tr></thead>
                        <tbody>
                        <% for (ListaEnlazada.Iterador<AlertaClimatica> _it = alertas.iterador(); _it.tieneSiguiente(); ) {
                               AlertaClimatica alerta = _it.siguiente();
                               if (alerta.getHoraFin() == null) { %>
                        <tr>
                            <td><span class="badge bg-danger"><%= alerta.getTipo() != null ? alerta.getTipo().name().replace("_", " ") : "—" %></span></td>
                            <td class="small"><%= alerta.getHoraInicio() != null ? alerta.getHoraInicio().toString().replace("T", " ").substring(0, 16) : "—" %></td>
                            <td>
                                <% ListaEnlazada<String> afectadas = alerta.getIdsDeAtraccionesAfectadas();
                                   if (afectadas != null && !afectadas.estaVacia()) { %><span class="badge bg-secondary"><%= afectadas.tamanio() %> atracción(es)</span>
                                <% } else { %><span class="text-muted small">Ninguna</span><% } %>
                            </td>
                            <td><form method="post" action="${pageContext.request.contextPath}/alert/cancel" onsubmit="return confirm('¿Cancelar esta alerta?')">
                                <input type="hidden" name="id" value="<%= alerta.getId() %>">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Cancelar</button>
                            </form></td>
                        </tr>
                        <% } } %>
                        </tbody>
                    </table>
                    <% } %>
                </div>
            </div>

            <!-- Historial -->
            <% boolean hayHistorial = false;
               if (alertas != null) {
                   for (ListaEnlazada.Iterador<AlertaClimatica> _it2 = alertas.iterador(); _it2.tieneSiguiente(); ) {
                       if (_it2.siguiente().getHoraFin() != null) { hayHistorial = true; break; }
                   }
               }
               if (hayHistorial) { %>
            <div class="card shadow-sm mt-3">
                <div class="card-header bg-secondary text-white"><h6 class="mb-0">Historial de Alertas</h6></div>
                <div class="card-body p-0">
                    <table class="table table-sm mb-0">
                        <thead class="table-light"><tr><th>Tipo</th><th>Inicio</th><th>Fin</th></tr></thead>
                        <tbody>
                        <% for (ListaEnlazada.Iterador<AlertaClimatica> _it3 = alertas.iterador(); _it3.tieneSiguiente(); ) {
                               AlertaClimatica alerta = _it3.siguiente();
                               if (alerta.getHoraFin() != null) { %>
                        <tr class="text-muted">
                            <td><%= alerta.getTipo() != null ? alerta.getTipo().name().replace("_", " ") : "—" %></td>
                            <td class="small"><%= alerta.getHoraInicio() != null ? alerta.getHoraInicio().toString().replace("T", " ").substring(0, 16) : "—" %></td>
                            <td class="small"><%= alerta.getHoraFin().toString().replace("T", " ").substring(0, 16) %></td>
                        </tr>
                        <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
