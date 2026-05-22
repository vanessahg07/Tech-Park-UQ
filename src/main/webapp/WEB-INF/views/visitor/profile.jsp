<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%@ page import="co.edu.uq.techpark.model.Tiquete" %>
<%@ page import="java.util.Base64" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Visitante visitante = (Visitante) session.getAttribute("user");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    // Flash messages from redirects
    String flashMessage = (String) session.getAttribute("flashMessage");
    String flashError   = (String) session.getAttribute("flashError");
    if (flashMessage != null) session.removeAttribute("flashMessage");
    if (flashError   != null) session.removeAttribute("flashError");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mi Perfil — Tech-Park UQ</title>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/visitor/profile">Mi Perfil</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/ticket/purchase">Tiquetes</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/queue/status">Mi Cola</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/map">Mapa</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/history">Historial</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/favorites">Favoritos</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/notifications">Notificaciones</a></li>
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
    <h2 class="mb-4">Mi Perfil</h2>

    <% if (error != null) { %>
    <div class="alert alert-danger"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
    <div class="alert alert-success"><%= success %></div>
    <% } %>
    <% if (flashMessage != null) { %>
    <div class="alert alert-success alert-dismissible fade show"><%= flashMessage %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <% } %>
    <% if (flashError != null) { %>
    <div class="alert alert-danger alert-dismissible fade show"><%= flashError %><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <% } %>

    <div class="row g-4">
        <!-- Datos del perfil -->
        <div class="col-md-4 text-center">
            <% if (visitante.getFoto() != null && visitante.getFoto().length > 0) { %>
            <img src="data:image/jpeg;base64,<%= Base64.getEncoder().encodeToString(visitante.getFoto()) %>"
                 class="rounded-circle img-thumbnail mb-3" style="width:160px;height:160px;object-fit:cover;"
                 alt="Foto de perfil">
            <% } else { %>
            <div class="rounded-circle bg-secondary d-flex align-items-center justify-content-center mx-auto mb-3"
                 style="width:160px;height:160px;">
                <span class="text-white fs-1">👤</span>
            </div>
            <% } %>
            <h4><%= visitante.getNombreCompleto() != null ? visitante.getNombreCompleto() : "—" %></h4>
            <span class="badge bg-primary">Visitante</span>
        </div>

        <div class="col-md-8">
            <div class="card mb-3">
                <div class="card-header bg-white fw-semibold">Datos personales</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-4">Documento</dt>
                        <dd class="col-sm-8"><%= visitante.getNumeroDocumento() != null ? visitante.getNumeroDocumento() : "—" %></dd>
                        <dt class="col-sm-4">Edad</dt>
                        <dd class="col-sm-8"><%= visitante.getEdad() %> años</dd>
                        <dt class="col-sm-4">Estatura</dt>
                        <dd class="col-sm-8"><%= visitante.getEstaturaCm() %> cm</dd>
                        <dt class="col-sm-4">Saldo virtual</dt>
                        <dd class="col-sm-8">
                            <span class="badge bg-success fs-6">
                                $<%= String.format("%,.2f", visitante.getSaldoVirtual()) %>
                            </span>
                        </dd>
                    </dl>
                </div>
            </div>

            <!-- Tiquete activo -->
            <% Tiquete tiquete = visitante.getTiqueteActivo(); %>
            <% if (tiquete != null) { %>
            <div class="card mb-3 border-success">
                <div class="card-header bg-success text-white fw-semibold">Tiquete activo</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-4">Tipo</dt>
                        <dd class="col-sm-8"><%= tiquete.getTipo() %></dd>
                        <dt class="col-sm-4">Precio</dt>
                        <dd class="col-sm-8">$<%= String.format("%,.2f", tiquete.getPrecio()) %></dd>
                        <dt class="col-sm-4">Prioridad</dt>
                        <dd class="col-sm-8">
                            <% if (tiquete.getPrioridad() == 1) { %>
                            <span class="badge bg-warning text-dark">Fast-Pass (1)</span>
                            <% } else { %>
                            <span class="badge bg-secondary">General (2)</span>
                            <% } %>
                        </dd>
                    </dl>
                </div>
            </div>
            <% } else { %>
            <div class="alert alert-warning">No tienes un tiquete activo. <a href="${pageContext.request.contextPath}/ticket/purchase">Comprar tiquete</a></div>
            <% } %>
        </div>
    </div>

    <!-- Recarga de saldo virtual -->
    <div class="card mt-4 border-success">
        <div class="card-header bg-success text-white fw-semibold">💳 Recargar saldo virtual</div>
        <div class="card-body">
            <p class="text-muted small mb-3">
                Saldo actual: <strong class="text-success">$<%= String.format("%,.2f", visitante.getSaldoVirtual()) %></strong>
            </p>
            <form method="post" action="${pageContext.request.contextPath}/visitor/recharge" class="row g-2 align-items-end">
                <div class="col-sm-6">
                    <label for="rechargeAmount" class="form-label">Monto a recargar ($)</label>
                    <input type="number" class="form-control" id="rechargeAmount" name="amount"
                           min="1000" step="1000" placeholder="Ej: 50000" required>
                </div>
                <div class="col-sm-auto">
                    <button type="submit" class="btn btn-success">Recargar</button>
                </div>
            </form>
            <div class="mt-2 d-flex flex-wrap gap-2">
                <button type="button" class="btn btn-outline-success btn-sm preset-btn" data-amount="20000">+ $20.000</button>
                <button type="button" class="btn btn-outline-success btn-sm preset-btn" data-amount="50000">+ $50.000</button>
                <button type="button" class="btn btn-outline-success btn-sm preset-btn" data-amount="100000">+ $100.000</button>
            </div>
        </div>
    </div>

    <!-- Formulario de actualización -->
    <div class="card mt-4">
        <div class="card-header bg-white fw-semibold">Actualizar perfil</div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/visitor/update" enctype="multipart/form-data">
                <div class="mb-3">
                    <label for="fullName" class="form-label">Nombre completo</label>
                    <input type="text" class="form-control" id="fullName" name="fullName"
                           value="<%= visitante.getNombreCompleto() != null ? visitante.getNombreCompleto() : "" %>" required>
                </div>
                <div class="mb-3">
                    <label for="photo" class="form-label">Foto de perfil</label>
                    <input type="file" class="form-control" id="photo" name="photo" accept="image/*">
                    <div class="form-text">Opcional. Formatos: JPG, PNG.</div>
                </div>
                <button type="submit" class="btn btn-primary">Guardar cambios</button>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Botones de recarga rápida
    document.querySelectorAll('.preset-btn').forEach(function(btn) {
        btn.addEventListener('click', function() {
            document.getElementById('rechargeAmount').value = this.dataset.amount;
        });
    });
</script>
</body>
</html>
