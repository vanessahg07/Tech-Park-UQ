<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Atraccion" %>
<%@ page import="co.edu.uq.techpark.model.EstadoAtraccion" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%@ page import="co.edu.uq.techpark.ds.ArbolBinarioBusqueda" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    ListaEnlazada<Atraccion> atracciones = (ListaEnlazada<Atraccion>) request.getAttribute("attractions");
    ArbolBinarioBusqueda<String, Integer> estadosCola = (ArbolBinarioBusqueda<String, Integer>) request.getAttribute("queueStatuses");
    String error = (String) request.getAttribute("error");
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
    <title>Mi Cola — Tech-Park UQ</title>
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
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/ticket/purchase">Tiquetes</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/queue/status">Mi Cola</a></li>
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
    <h2 class="mb-4">Cola virtual</h2>

    <% if (error != null) { %>
    <div class="alert alert-danger"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
    <div class="alert alert-success"><%= success %></div>
    <% } %>

    <% if (atracciones == null || atracciones.estaVacia()) { %>
    <div class="alert alert-info">No hay atracciones disponibles en este momento.</div>
    <% } else { %>
    <div class="row g-3">
        <% for (ListaEnlazada.Iterador<Atraccion> _ait = atracciones.iterador(); _ait.tieneSiguiente(); ) {
            Atraccion attr = _ait.siguiente();
            boolean estaActiva = EstadoAtraccion.ACTIVA.equals(attr.getEstado());
            Integer posicion = estadosCola != null ? estadosCola.buscar(attr.getId()) : null;
            boolean enCola = posicion != null && posicion > 0;
            int tiempoEspera = attr.getCapacidadMaximaPorCiclo() > 0
                ? (posicion != null ? (posicion / attr.getCapacidadMaximaPorCiclo()) * attr.getMinutosEsperaEstimados() : 0)
                : 0;
        %>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 <%= enCola ? "border-primary" : "" %>">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-semibold"><%= attr.getNombre() %></span>
                    <% if (estaActiva) { %>
                    <span class="badge bg-success">Activa</span>
                    <% } else if (EstadoAtraccion.EN_MANTENIMIENTO.equals(attr.getEstado())) { %>
                    <span class="badge bg-warning text-dark">Mantenimiento</span>
                    <% } else { %>
                    <span class="badge bg-danger">Cerrada</span>
                    <% } %>
                </div>
                <div class="card-body">
                    <p class="small text-muted mb-2">
                        Capacidad/ciclo: <%= attr.getCapacidadMaximaPorCiclo() %> |
                        Espera est.: <%= attr.getMinutosEsperaEstimados() %> min
                    </p>
                    <% if (attr.getEstaturaMinimaEnCm() > 0) { %>
                    <p class="small text-muted mb-1">Estatura mínima: <%= attr.getEstaturaMinimaEnCm() %> cm</p>
                    <% } %>
                    <% if (attr.getEdadMinima() > 0) { %>
                    <p class="small text-muted mb-2">Edad mínima: <%= attr.getEdadMinima() %> años</p>
                    <% } %>

                    <% if (enCola) { %>
                    <div class="alert alert-primary py-2 mb-2">
                        <strong>Posición en cola:</strong> #<%= posicion %><br>
                        <strong>Tiempo estimado:</strong> ~<%= tiempoEspera %> min
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/queue/cancel" class="mb-2">
                        <input type="hidden" name="attractionId" value="<%= attr.getId() %>">
                        <button type="submit" class="btn btn-outline-danger btn-sm w-100">✖ Cancelar posición</button>
                    </form>
                    <% } else if (estaActiva) { %>
                    <form method="post" action="${pageContext.request.contextPath}/queue/join" class="mb-2">
                        <input type="hidden" name="attractionId" value="<%= attr.getId() %>">
                        <button type="submit" class="btn btn-primary btn-sm w-100">+ Unirse a la cola</button>
                    </form>
                    <% } else { %>
                    <button class="btn btn-secondary btn-sm w-100 mb-2" disabled>No disponible</button>
                    <% } %>
                    
                    <!-- Botón de favoritos -->
                    <form method="post" action="${pageContext.request.contextPath}/visitor/favorites/add">
                        <input type="hidden" name="attractionId" value="<%= attr.getId() %>">
                        <button type="submit" class="btn btn-outline-warning btn-sm w-100">
                            ⭐ Agregar a favoritos
                        </button>
                    </form>
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
