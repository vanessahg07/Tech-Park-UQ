<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%@ page import="co.edu.uq.techpark.model.Atraccion" %>
<%@ page import="co.edu.uq.techpark.model.EstadoAtraccion" %>
<%@ page import="co.edu.uq.techpark.model.TipoAtraccion" %>
<%@ page import="co.edu.uq.techpark.ds.GrafoDelParque" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
    ListaEnlazada<Atraccion> atracciones = (ListaEnlazada<Atraccion>) request.getAttribute("attractions");
    GrafoDelParque<String> grafo = (GrafoDelParque<String>) request.getAttribute("graph");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Atracciones</title>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/attraction/list">Atracciones</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/staff/list">Personal</a></li>
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
<div class="container-fluid py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div><h2 class="mb-0">Gestión de Atracciones</h2><p class="text-muted mb-0">Administra el catálogo de atracciones del parque</p></div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalNuevaAtraccion">+ Nueva Atracción</button>
    </div>
    <% if (error != null) { %><div class="alert alert-danger alert-dismissible fade show"><%= error %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
    <% if (success != null) { %><div class="alert alert-success alert-dismissible fade show"><%= success %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>

    <div class="card shadow-sm mb-4">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-dark">
                        <tr><th>Nombre</th><th>Tipo</th><th>Estado</th><th>Min. Estatura</th><th>Min. Edad</th><th>Cap./Ciclo</th><th>Visitantes Acum.</th><th>Acciones</th></tr>
                    </thead>
                    <tbody>
                    <% if (atracciones == null || atracciones.estaVacia()) { %>
                        <tr><td colspan="8" class="text-center text-muted py-4">No hay atracciones registradas.</td></tr>
                    <% } else {
                        for (ListaEnlazada.Iterador<Atraccion> _it = atracciones.iterador(); _it.tieneSiguiente(); ) {
                            Atraccion a = _it.siguiente();
                            String estadoNombre = a.getEstado() != null ? a.getEstado().name() : "CERRADA";
                            String estadoLabel2;
                            String estadoStyle2;
                            if ("ACTIVA".equals(estadoNombre)) {
                                estadoLabel2 = "Activa"; estadoStyle2 = "background:#4a7c59;color:#fff;";
                            } else if ("EN_MANTENIMIENTO".equals(estadoNombre)) {
                                estadoLabel2 = "Mantenimiento"; estadoStyle2 = "background:#BC6C25;color:#fff;";
                            } else {
                                estadoLabel2 = "Cerrada"; estadoStyle2 = "background:#9b3a3a;color:#fff;";
                            }
                    %>
                        <tr>
                            <td class="fw-semibold"><%= a.getNombre() %></td>
                            <td><span class="badge bg-secondary"><%= a.getTipo() != null ? a.getTipo().name() : "-" %></span></td>
                            <td><span class="badge" style="<%= estadoStyle2 %>"><%= estadoLabel2 %></span></td>
                            <td><%= a.getEstaturaMinimaEnCm() > 0 ? a.getEstaturaMinimaEnCm() + " cm" : "—" %></td>
                            <td><%= a.getEdadMinima() > 0 ? a.getEdadMinima() + " años" : "—" %></td>
                            <td><%= a.getCapacidadMaximaPorCiclo() %></td>
                            <td><%= a.getVisitantesAcumulados() %></td>
                            <td>
                                <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editModal-<%= a.getId() %>">Editar</button>
                                <button class="btn btn-sm btn-outline-secondary" data-bs-toggle="collapse" data-bs-target="#estadoForm-<%= a.getId() %>">Estado</button>
                                <div class="collapse mt-1" id="estadoForm-<%= a.getId() %>">
                                    <form method="post" action="${pageContext.request.contextPath}/attraction/status" class="d-flex gap-1 flex-wrap align-items-end mt-1">
                                        <input type="hidden" name="attractionId" value="<%= a.getId() %>">
                                        <select name="status" class="form-select form-select-sm" style="width:auto">
                                            <option value="ACTIVA" <%= "ACTIVA".equals(estadoNombre) ? "selected" : "" %>>Activa</option>
                                            <option value="EN_MANTENIMIENTO" <%= "EN_MANTENIMIENTO".equals(estadoNombre) ? "selected" : "" %>>Mantenimiento</option>
                                            <option value="CERRADA" <%= "CERRADA".equals(estadoNombre) ? "selected" : "" %>>Cerrada</option>
                                        </select>
                                        <input type="text" name="reason" class="form-control form-control-sm" placeholder="Motivo" style="width:140px">
                                        <button type="submit" class="btn btn-sm btn-primary">Aplicar</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Conexiones del grafo -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">🗺️ Conexiones del Mapa</h5>
            <button class="btn btn-sm btn-outline-light" data-bs-toggle="modal" data-bs-target="#modalNuevaConexion">+ Agregar Conexión</button>
        </div>
        <div class="card-body p-0">
            <table class="table table-sm mb-0">
                <thead class="table-secondary"><tr><th>Desde</th><th>Hasta</th><th>Distancia (m)</th><th>Acción</th></tr></thead>
                <tbody>
                <% boolean hayAristas = false;
                   if (atracciones != null && grafo != null) {
                       for (ListaEnlazada.Iterador<Atraccion> _it2 = atracciones.iterador(); _it2.tieneSiguiente(); ) {
                           Atraccion a2 = _it2.siguiente();
                           ListaEnlazada<GrafoDelParque.Arista<String>> listaAristas = grafo.obtenerAristas(a2.getId());
                           if (listaAristas != null) {
                               for (ListaEnlazada.Iterador<GrafoDelParque.Arista<String>> _ei = listaAristas.iterador(); _ei.tieneSiguiente(); ) {
                                   GrafoDelParque.Arista<String> arista = _ei.siguiente();
                                   if (a2.getId().compareTo(arista.destino) < 0) {
                                       hayAristas = true;
                                       String nombreDestino = arista.destino;
                                       for (ListaEnlazada.Iterador<Atraccion> _it3 = atracciones.iterador(); _it3.tieneSiguiente(); ) {
                                           Atraccion d = _it3.siguiente();
                                           if (d.getId().equals(arista.destino)) { nombreDestino = d.getNombre(); break; }
                                       } %>
                <tr>
                    <td><%= a2.getNombre() %></td><td><%= nombreDestino %></td><td><%= arista.pesoEnMetros %> m</td>
                    <td><form method="post" action="${pageContext.request.contextPath}/attraction/edge/remove" class="d-inline">
                        <input type="hidden" name="fromId" value="<%= a2.getId() %>"><input type="hidden" name="toId" value="<%= arista.destino %>">
                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('¿Eliminar?')">✖</button>
                    </form></td>
                </tr>
                <%              }
                           } // fin for _ei
                       } // fin if listaAristas
                   } // fin for _it2
                   } // fin if atracciones
                   if (!hayAristas) { %><tr><td colspan="4" class="text-center text-muted py-3">Sin conexiones registradas.</td></tr><% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modales de Edición de Atracciones -->
<% if (atracciones != null && !atracciones.estaVacia()) {
    for (ListaEnlazada.Iterador<Atraccion> _it = atracciones.iterador(); _it.tieneSiguiente(); ) {
        Atraccion a = _it.siguiente();
%>
<div class="modal fade" id="editModal-<%= a.getId() %>" tabindex="-1">
    <div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><h5 class="modal-title">Editar: <%= a.getNombre() %></h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form method="post" action="${pageContext.request.contextPath}/attraction/update">
            <div class="modal-body">
                <input type="hidden" name="id" value="<%= a.getId() %>">
                <input type="hidden" name="zoneId" value="<%= a.getIdZona() != null ? a.getIdZona() : "" %>">
                <div class="mb-2"><label class="form-label">Nombre</label><input type="text" name="name" class="form-control" value="<%= a.getNombre() %>" required></div>
                <div class="mb-2"><label class="form-label">Tipo</label>
                    <select name="type" class="form-select">
                        <% for (TipoAtraccion t : TipoAtraccion.values()) { %><option value="<%= t.name() %>" <%= t.equals(a.getTipo()) ? "selected" : "" %>><%= t.name() %></option><% } %>
                    </select></div>
                <div class="row g-2">
                    <div class="col-6"><label class="form-label">Min. Estatura (cm)</label><input type="number" name="minHeightCm" class="form-control" value="<%= a.getEstaturaMinimaEnCm() %>" min="0"></div>
                    <div class="col-6"><label class="form-label">Min. Edad</label><input type="number" name="minAge" class="form-control" value="<%= a.getEdadMinima() %>" min="0"></div>
                    <div class="col-6"><label class="form-label">Cap. por Ciclo</label><input type="number" name="maxCapacityPerCycle" class="form-control" value="<%= a.getCapacidadMaximaPorCiclo() %>" min="1"></div>
                </div>
            </div>
            <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button><button type="submit" class="btn btn-primary">Guardar</button></div>
        </form>
    </div></div>
</div>
<% } } %>

<!-- Modal: Nueva Atracción -->
<div class="modal fade" id="modalNuevaAtraccion" tabindex="-1">
    <div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><h5 class="modal-title">Nueva Atracción</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form method="post" action="${pageContext.request.contextPath}/attraction/add">
            <div class="modal-body">
                <div class="mb-2"><label class="form-label">Nombre <span class="text-danger">*</span></label><input type="text" name="name" class="form-control" required></div>
                <div class="mb-2"><label class="form-label">Tipo</label>
                    <select name="type" class="form-select"><% for (TipoAtraccion t : TipoAtraccion.values()) { %><option value="<%= t.name() %>"><%= t.name() %></option><% } %></select></div>
                <div class="row g-2">
                    <div class="col-6"><label class="form-label">Min. Estatura (cm)</label><input type="number" name="minHeightCm" class="form-control" value="0" min="0"></div>
                    <div class="col-6"><label class="form-label">Min. Edad</label><input type="number" name="minAge" class="form-control" value="0" min="0"></div>
                    <div class="col-6"><label class="form-label">Cap. por Ciclo <span class="text-danger">*</span></label><input type="number" name="maxCapacityPerCycle" class="form-control" value="10" min="1" required></div>
                </div>
            </div>
            <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button><button type="submit" class="btn btn-primary">Crear</button></div>
        </form>
    </div></div>
</div>

<!-- Modal: Agregar Conexión -->
<div class="modal fade" id="modalNuevaConexion" tabindex="-1">
    <div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><h5 class="modal-title">Nueva Conexión</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form method="post" action="${pageContext.request.contextPath}/attraction/edge/add">
            <div class="modal-body">
                <div class="mb-2"><label class="form-label">Desde <span class="text-danger">*</span></label>
                    <select name="fromId" class="form-select" required><option value="">— Seleccionar —</option>
                        <% if (atracciones != null) { for (ListaEnlazada.Iterador<Atraccion> _oa = atracciones.iterador(); _oa.tieneSiguiente(); ) { Atraccion ax = _oa.siguiente(); %><option value="<%= ax.getId() %>"><%= ax.getNombre() %></option><% } } %>
                    </select></div>
                <div class="mb-2"><label class="form-label">Hasta <span class="text-danger">*</span></label>
                    <select name="toId" class="form-select" required><option value="">— Seleccionar —</option>
                        <% if (atracciones != null) { for (ListaEnlazada.Iterador<Atraccion> _ob = atracciones.iterador(); _ob.tieneSiguiente(); ) { Atraccion ax = _ob.siguiente(); %><option value="<%= ax.getId() %>"><%= ax.getNombre() %></option><% } } %>
                    </select></div>
                <div class="mb-2"><label class="form-label">Distancia (metros) <span class="text-danger">*</span></label><input type="number" name="weight" class="form-control" min="1" value="100" required></div>
            </div>
            <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button><button type="submit" class="btn btn-primary">Agregar</button></div>
        </form>
    </div></div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
