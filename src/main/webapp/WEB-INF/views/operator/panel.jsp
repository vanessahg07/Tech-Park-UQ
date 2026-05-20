<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%@ page import="co.edu.uq.techpark.model.Zona" %>
<%@ page import="co.edu.uq.techpark.model.Atraccion" %>
<%@ page import="co.edu.uq.techpark.model.EstadoAtraccion" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%@ page import="co.edu.uq.techpark.ds.ArbolBinarioBusqueda" %>
<%
    if (session == null || !"OPERATOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Personal operator = (Personal) session.getAttribute("user");
    Zona zone = (Zona) request.getAttribute("zone");
    ListaEnlazada<Atraccion> attractions = (ListaEnlazada<Atraccion>) request.getAttribute("attractions");
    ArbolBinarioBusqueda<String, Integer> queueSizes = (ArbolBinarioBusqueda<String, Integer>) request.getAttribute("queueSizes");
    String errorMsg = (String) request.getAttribute("error");
    String successMsg = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Panel de Operador</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg ">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/dashboard">🎢 Tech-Park UQ</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Inicio</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/operator/panel">Mi Panel</a>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <span class="nav-link text-white-50">
                        <%= operator != null ? operator.getNombreCompleto() : "" %> (OPERADOR)
                    </span>
                </li>
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

    <!-- Encabezado del operador -->
    <div class="d-flex align-items-center gap-3 mb-4">
        <div>
            <h2 class="mb-0">Panel de Operador</h2>
            <p class="text-muted mb-0">
                Operador: <strong><%= operator != null ? operator.getNombreCompleto() : "—" %></strong>
                &nbsp;|&nbsp;
                Zona asignada: <strong><%= zone != null ? zone.getNombre() : "Sin zona asignada" %></strong>
                <% if (zone != null) { %>
                &nbsp;
                <span class="badge bg-secondary">
                    <%= zone.getVisitantesActuales() %> / <%= zone.getAforoMaximo() %> visitantes
                </span>
                <% } %>
            </p>
        </div>
    </div>

    <!-- Mensajes de éxito / error -->
    <% if (successMsg != null && !successMsg.isEmpty()) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <%= successMsg %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>
    <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <%= errorMsg %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <!-- Sin zona asignada -->
    <% if (zone == null) { %>
    <div class="alert alert-warning">
        No tiene una zona asignada. Contacte al administrador.
    </div>
    <% } else if (attractions == null || attractions.estaVacia()) { %>
    <div class="alert alert-info">
        No hay atracciones registradas en su zona.
    </div>
    <% } else { %>

    <!-- Tarjetas de atracciones -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
        <% for (ListaEnlazada.Iterador<Atraccion> _ait = attractions.iterador(); _ait.tieneSiguiente(); ) {
            Atraccion attr = _ait.siguiente();
            EstadoAtraccion attrStatus = attr.getEstado();
            String statusName = attrStatus != null ? attrStatus.name() : "CERRADA";
            String statusLabel;
            if ("ACTIVA".equals(statusName))              { statusLabel = "Activa"; }
            else if ("EN_MANTENIMIENTO".equals(statusName)) { statusLabel = "Mantenimiento"; }
            else                                            { statusLabel = "Cerrada"; }

            String typeName = attr.getTipo() != null ? attr.getTipo().name() : "";
            String typeLabel;
            if ("ACUATICA".equals(typeName))              { typeLabel = "Acuática"; }
            else if ("MECANICA_DE_ALTURA".equals(typeName)) { typeLabel = "Mecánica de Altura"; }
            else if ("MECANICA".equals(typeName))          { typeLabel = "Mecánica"; }
            else if ("ESPECTACULO".equals(typeName))       { typeLabel = "Espectáculo"; }
            else                                           { typeLabel = "Otra"; }

            int queueSize = (queueSizes != null && queueSizes.contiene(attr.getId()))
                            ? queueSizes.buscar(attr.getId()) : 0;
            boolean isActive = EstadoAtraccion.ACTIVA.equals(attrStatus);
        %>
        <div class="col">
            <div class="card attraction-card h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h6 class="mb-0 fw-bold"><%= attr.getNombre() %></h6>
                    <span class="badge badge-<%= statusName %> rounded-pill"><%= statusLabel %></span>
                </div>
                <div class="card-body">

                    <!-- Tipo y cola -->
                    <p class="mb-1 small text-muted">Tipo: <strong><%= typeLabel %></strong></p>
                    <p class="mb-1 small text-muted">
                        Capacidad por ciclo: <strong><%= attr.getCapacidadMaximaPorCiclo() %></strong>
                    </p>
                    <div class="d-flex align-items-center gap-2 mb-3 mt-2">
                        <span class="fs-5">🧍</span>
                        <span class="fw-semibold"><%= queueSize %></span>
                        <span class="text-muted small">visitante<%= queueSize != 1 ? "s" : "" %> en cola</span>
                    </div>

                    <!-- Botón procesar ciclo (solo si ACTIVE) -->
                    <% if (isActive) { %>
                    <form method="post" action="${pageContext.request.contextPath}/queue/cycle" class="mb-3">
                        <input type="hidden" name="attractionId" value="<%= attr.getId() %>">
                        <button type="submit" class="btn btn-success btn-sm w-100">
                            ▶ Procesar ciclo
                        </button>
                    </form>
                    <% } else { %>
                    <button class="btn btn-secondary btn-sm w-100 mb-3" disabled>
                        ▶ Procesar ciclo
                    </button>
                    <% } %>

                    <!-- Cambio de estado inline -->
                    <form method="post" action="${pageContext.request.contextPath}/attraction/status">
                        <input type="hidden" name="attractionId" value="<%= attr.getId() %>">
                        <div class="mb-2">
                            <label class="form-label small mb-1">Cambiar estado</label>
                            <select name="status" class="form-select form-select-sm">
                                <option value="ACTIVA"           <%= "ACTIVA".equals(statusName)           ? "selected" : "" %>>Activa</option>
                                <option value="EN_MANTENIMIENTO" <%= "EN_MANTENIMIENTO".equals(statusName) ? "selected" : "" %>>Mantenimiento</option>
                                <option value="CERRADA"          <%= "CERRADA".equals(statusName)          ? "selected" : "" %>>Cerrada</option>
                            </select>
                        </div>
                        <div class="mb-2">
                            <input type="text" name="reason" class="form-control form-control-sm"
                                   placeholder="Motivo (opcional)">
                        </div>
                        <button type="submit" class="btn btn-outline-primary btn-sm w-100">
                            Actualizar estado
                        </button>
                    </form>

                </div>
                <div class="card-footer text-muted small">
                    Visitantes acumulados: <%= attr.getVisitantesAcumulados() %>
                    <% if (attr.getMotivoCierre() != null && !attr.getMotivoCierre().isEmpty()) { %>
                    &nbsp;·&nbsp; Motivo cierre: <%= attr.getMotivoCierre() %>
                    <% } %>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
