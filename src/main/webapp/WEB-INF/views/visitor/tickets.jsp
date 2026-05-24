<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%@ page import="co.edu.uq.techpark.model.Tiquete" %>
<%@ page import="co.edu.uq.techpark.model.TipoTiquete" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Visitante visitante = (Visitante) request.getAttribute("visitor");
    if (visitante == null) visitante = (Visitante) session.getAttribute("user");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    Tiquete tiqueteActivo = visitante != null ? visitante.getTiqueteActivo() : null;
    int notifCount = (visitante != null && visitante.getNotificacionesSinLeer() != null)
                     ? visitante.getNotificacionesSinLeer().tamanio() : 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tickets — Tech-Park UQ</title>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/ticket/purchase">Tiquetes</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/queue/status">Mi Cola</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/map">Mapa</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/history">Historial</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/favorites">Favoritos</a></li>
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
    <h2 class="mb-4">Tiquetes de acceso</h2>

    <% if (error != null) { %>
    <div class="alert alert-danger"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
    <div class="alert alert-success"><%= success %></div>
    <% } %>

    <!-- Ticket activo -->
    <% if (tiqueteActivo != null) { %>
    <div class="alert alert-success d-flex align-items-center gap-2">
        <span class="fs-4">🎫</span>
        <div>
            <strong>Tiquete activo:</strong> <%= tiqueteActivo.getTipo() %> —
            Precio: $<%= String.format("%,.2f", tiqueteActivo.getPrecio()) %> —
            Prioridad: <%= tiqueteActivo.getPrioridad() == 1 ? "Fast-Pass" : "General" %>
        </div>
    </div>
    <% } %>

    <!-- Tipos de ticket disponibles -->
    <div class="row g-4 mb-4">
        <!-- GENERAL -->
        <div class="col-md-4">
            <div class="card h-100 border-secondary">
                <div class="card-header bg-secondary text-white text-center fw-bold">
                    🎟️ General
                </div>
                <div class="card-body">
                    <p class="card-text">Acceso estándar al parque. Ingreso a todas las atracciones disponibles según disponibilidad.</p>
                    <ul class="list-unstyled small text-muted">
                        <li>✔ Acceso a todas las zonas</li>
                        <li>✔ Cola virtual prioridad 2</li>
                        <li>✔ Precio base</li>
                    </ul>
                    <p class="fw-semibold">Precio estimado: <span class="text-success">$50.000</span></p>
                </div>
            </div>
        </div>
        <!-- FAMILIAR -->
        <div class="col-md-4">
            <div class="card h-100 border-primary">
                <div class="card-header bg-primary text-white text-center fw-bold">
                    👨‍👩‍👧 Familiar
                </div>
                <div class="card-body">
                    <p class="card-text">Ideal para grupos familiares. Incluye descuento sobre el precio base del tiquete General.</p>
                    <ul class="list-unstyled small text-muted">
                        <li>✔ Acceso a todas las zonas</li>
                        <li>✔ Cola virtual prioridad 2</li>
                        <li>✔ Descuento familiar aplicado</li>
                    </ul>
                    <p class="fw-semibold">Precio estimado: <span class="text-success">$42.500</span></p>
                </div>
            </div>
        </div>
        <!-- FAST_PASS -->
        <div class="col-md-4">
            <div class="card h-100 border-warning">
                <div class="card-header bg-warning text-dark text-center fw-bold">
                    ⚡ Fast-Pass
                </div>
                <div class="card-body">
                    <p class="card-text">Acceso prioritario. Salta la fila en todas las atracciones con prioridad máxima en la cola virtual.</p>
                    <ul class="list-unstyled small text-muted">
                        <li>✔ Acceso a todas las zonas</li>
                        <li>✔ Cola virtual prioridad 1 (máxima)</li>
                        <li>✔ Atención preferencial</li>
                    </ul>
                    <p class="fw-semibold">Precio estimado: <span class="text-success">$80.000</span></p>
                </div>
            </div>
        </div>
    </div>

    <!-- Formulario de compra -->
    <div class="card">
        <div class="card-header bg-white fw-semibold">Comprar tiquete</div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/ticket/purchase">
                <div class="mb-3">
                    <label for="ticketType" class="form-label">Tipo de tiquete</label>
                    <select class="form-select" id="ticketType" name="ticketType" required>
                        <option value="" disabled selected>Selecciona un tipo...</option>
                        <option value="GENERAL">General — $50.000</option>
                        <option value="FAMILIAR">Familiar — $42.500</option>
                        <option value="PASE_RAPIDO">Fast-Pass — $80.000</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">
                    🎫 Comprar tiquete
                </button>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
