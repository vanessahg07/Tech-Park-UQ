<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%@ page import="co.edu.uq.techpark.model.RolPersonal" %>
<%@ page import="co.edu.uq.techpark.model.Zona" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
    ListaEnlazada<Personal> listaPersonal = (ListaEnlazada<Personal>) request.getAttribute("staffList");
    ListaEnlazada<Zona> zonas = (ListaEnlazada<Zona>) request.getAttribute("zones");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Personal</title>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/staff/list">Personal</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/alert/list">Alertas</a></li>
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
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div><h2 class="mb-0">Gestión de Personal</h2><p class="text-muted mb-0">Operadores y administradores del parque</p></div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalCrearPersonal">+ Nuevo Operador</button>
    </div>
    <% if (error != null) { %><div class="alert alert-danger alert-dismissible fade show"><%= error %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
    <% if (success != null) { %><div class="alert alert-success alert-dismissible fade show"><%= success %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>

    <div class="row g-4">
        <div class="col-lg-8">
            <% if (zonas != null && !zonas.estaVacia()) {
                for (ListaEnlazada.Iterador<Zona> _zit = zonas.iterador(); _zit.tieneSiguiente(); ) { Zona zona = _zit.siguiente(); %>
            <div class="card mb-3 shadow-sm">
                <div class="card-header bg-success text-white">
                    <h6 class="mb-0">📍 Zona: <%= zona.getNombre() %>
                        <small class="ms-2 opacity-75">(Aforo: <%= zona.getVisitantesActuales() %> / <%= zona.getAforoMaximo() %>)</small>
                    </h6>
                </div>
                <div class="card-body p-0">
                    <table class="table table-sm mb-0">
                        <thead class="table-light"><tr><th>Nombre</th><th>Usuario</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead>
                        <tbody>
                        <% boolean hayPersonal = false;
                           if (listaPersonal != null) {
                               for (ListaEnlazada.Iterador<Personal> _sit = listaPersonal.iterador(); _sit.tieneSiguiente(); ) {
                                   Personal p = _sit.siguiente();
                                   if (zona.getId().equals(p.getIdZonaAsignada())) { hayPersonal = true; %>
                        <tr class="<%= !p.isActivo() ? "table-secondary text-muted" : "" %>">
                            <td><%= p.getNombreCompleto() %></td>
                            <td><code><%= p.getNombreUsuario() %></code></td>
                            <td><span class="badge <%= RolPersonal.ADMINISTRADOR.equals(p.getRol()) ? "bg-danger" : "bg-primary" %>"><%= p.getRol() %></span></td>
                            <td><span class="badge <%= p.isActivo() ? "bg-success" : "bg-secondary" %>"><%= p.isActivo() ? "Activo" : "Inactivo" %></span></td>
                            <td><% if (p.isActivo()) { %>
                                <form method="post" action="${pageContext.request.contextPath}/staff/deactivate" class="d-inline" onsubmit="return confirm('¿Desactivar?')">
                                    <input type="hidden" name="id" value="<%= p.getId() %>">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Desactivar</button>
                                </form><% } %></td>
                        </tr>
                        <%  } } } %>
                        <% if (!hayPersonal) { %><tr><td colspan="5" class="text-center text-muted py-2 small">Sin operadores asignados.</td></tr><% } %>
                        </tbody>
                    </table>
                </div>
            </div>
            <% } } else { %><div class="alert alert-info">No hay zonas registradas.</div><% } %>

            <!-- Sin zona asignada -->
            <% boolean haySinZona = false;
               if (listaPersonal != null) {
                   for (ListaEnlazada.Iterador<Personal> _sit2 = listaPersonal.iterador(); _sit2.tieneSiguiente(); ) {
                       Personal p = _sit2.siguiente();
                       if (p.getIdZonaAsignada() == null || p.getIdZonaAsignada().isEmpty()) { haySinZona = true; break; }
                   }
               }
               if (haySinZona) { %>
            <div class="card mb-3 shadow-sm">
                <div class="card-header bg-secondary text-white"><h6 class="mb-0">Sin zona asignada</h6></div>
                <div class="card-body p-0">
                    <table class="table table-sm mb-0">
                        <thead class="table-light"><tr><th>Nombre</th><th>Usuario</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead>
                        <tbody>
                        <% for (ListaEnlazada.Iterador<Personal> _sit3 = listaPersonal.iterador(); _sit3.tieneSiguiente(); ) {
                               Personal p = _sit3.siguiente();
                               if (p.getIdZonaAsignada() == null || p.getIdZonaAsignada().isEmpty()) { %>
                        <tr>
                            <td><%= p.getNombreCompleto() %></td><td><code><%= p.getNombreUsuario() %></code></td>
                            <td><span class="badge <%= RolPersonal.ADMINISTRADOR.equals(p.getRol()) ? "bg-danger" : "bg-primary" %>"><%= p.getRol() %></span></td>
                            <td><span class="badge <%= p.isActivo() ? "bg-success" : "bg-secondary" %>"><%= p.isActivo() ? "Activo" : "Inactivo" %></span></td>
                            <td><% if (p.isActivo()) { %><form method="post" action="${pageContext.request.contextPath}/staff/deactivate" class="d-inline" onsubmit="return confirm('¿Desactivar?')"><input type="hidden" name="id" value="<%= p.getId() %>"><button type="submit" class="btn btn-sm btn-outline-danger">Desactivar</button></form><% } %></td>
                        </tr>
                        <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
            <% } %>
        </div>

        <div class="col-lg-4">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white"><h6 class="mb-0">Asignar Operador a Zona</h6></div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/staff/assign">
                        <div class="mb-3"><label class="form-label">Operador</label>
                            <select name="staffId" class="form-select" required><option value="">— Seleccionar —</option>
                                <% if (listaPersonal != null) { for (ListaEnlazada.Iterador<Personal> _sit4 = listaPersonal.iterador(); _sit4.tieneSiguiente(); ) { Personal p = _sit4.siguiente(); if (p.isActivo()) { %><option value="<%= p.getId() %>"><%= p.getNombreCompleto() %> (<%= p.getNombreUsuario() %>)</option><% } } } %>
                            </select></div>
                        <div class="mb-3"><label class="form-label">Zona</label>
                            <select name="zoneId" class="form-select" required><option value="">— Seleccionar —</option>
                                <% if (zonas != null) { for (ListaEnlazada.Iterador<Zona> _zit2 = zonas.iterador(); _zit2.tieneSiguiente(); ) { Zona z = _zit2.siguiente(); %><option value="<%= z.getId() %>"><%= z.getNombre() %></option><% } } %>
                            </select></div>
                        <button type="submit" class="btn btn-primary w-100">Asignar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modalCrearPersonal" tabindex="-1">
    <div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><h5 class="modal-title">Nuevo Operador</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form method="post" action="${pageContext.request.contextPath}/staff/create">
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">Nombre completo <span class="text-danger">*</span></label><input type="text" name="fullName" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Nombre de usuario <span class="text-danger">*</span></label><input type="text" name="username" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Contraseña <span class="text-danger">*</span></label><input type="password" name="password" class="form-control" required minlength="6"></div>
                <div class="mb-3"><label class="form-label">Rol</label>
                    <select name="role" class="form-select"><option value="OPERADOR">OPERADOR</option><option value="ADMINISTRADOR">ADMINISTRADOR</option></select></div>
                <div class="mb-3"><label class="form-label">Zona (opcional)</label>
                    <select name="zoneId" class="form-select"><option value="">— Sin asignar —</option>
                        <% if (zonas != null) { for (ListaEnlazada.Iterador<Zona> _zit3 = zonas.iterador(); _zit3.tieneSiguiente(); ) { Zona z = _zit3.siguiente(); %><option value="<%= z.getId() %>"><%= z.getNombre() %></option><% } } %>
                    </select></div>
            </div>
            <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button><button type="submit" class="btn btn-primary">Crear</button></div>
        </form>
    </div></div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
