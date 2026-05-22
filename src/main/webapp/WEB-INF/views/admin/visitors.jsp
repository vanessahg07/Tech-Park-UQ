<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.Personal" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%
    if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
    }
    Personal usuarioActual = (Personal) session.getAttribute("user");
    ListaEnlazada<Visitante> visitantes = (ListaEnlazada<Visitante>) request.getAttribute("visitantes");
    String flashMessage = (String) request.getAttribute("flashMessage");
    String flashError   = (String) request.getAttribute("flashError");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Visitantes</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<nav class="navbar navbar-expand-lg">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/dashboard">🎢 Tech-Park UQ</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/attraction/list">Atracciones</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/staff/list">Personal</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/visitors">Visitantes</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/alert/list">Alertas</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/report">Reporte</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scenario">Escenario</a></li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <span class="nav-link text-white-50">
                        <%= usuarioActual != null ? usuarioActual.getNombreCompleto() : "" %> (ADMIN)
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
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="mb-0">Gestión de Visitantes</h2>
            <p class="text-muted mb-0">Consulta y recarga de saldo virtual</p>
        </div>
    </div>

    <% if (flashMessage != null) { %>
    <div class="alert alert-success alert-dismissible fade show">
        <%= flashMessage %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>
    <% if (flashError != null) { %>
    <div class="alert alert-danger alert-dismissible fade show">
        <%= flashError %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-header bg-white fw-semibold">
            👥 Visitantes registrados
            (<%= visitantes != null ? visitantes.tamanio() : 0 %>)
        </div>
        <div class="card-body p-0">
            <% if (visitantes == null || visitantes.estaVacia()) { %>
            <div class="p-4 text-center text-muted">No hay visitantes registrados.</div>
            <% } else { %>
            <div class="table-responsive">
                <table class="table table-hover mb-0 align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Nombre</th>
                            <th>Documento</th>
                            <th>Edad</th>
                            <th>Estatura</th>
                            <th>Saldo virtual</th>
                            <th>Tiquete activo</th>
                            <th>Recargar saldo</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (ListaEnlazada.Iterador<Visitante> it = visitantes.iterador(); it.tieneSiguiente(); ) {
                           Visitante v = it.siguiente(); %>
                    <tr>
                        <td><%= v.getNombreCompleto() != null ? v.getNombreCompleto() : "—" %></td>
                        <td><code><%= v.getNumeroDocumento() %></code></td>
                        <td><%= v.getEdad() %> años</td>
                        <td><%= v.getEstaturaCm() %> cm</td>
                        <td>
                            <span class="badge bg-success fs-6">
                                $<%= String.format("%,.2f", v.getSaldoVirtual()) %>
                            </span>
                        </td>
                        <td>
                            <% if (v.getTiqueteActivo() != null) { %>
                            <span class="badge <%= v.getTiqueteActivo().getPrioridad() == 1 ? "bg-warning text-dark" : "bg-secondary" %>">
                                <%= v.getTiqueteActivo().getTipo() %>
                            </span>
                            <% } else { %>
                            <span class="text-muted small">Sin tiquete</span>
                            <% } %>
                        </td>
                        <td>
                            <button class="btn btn-outline-success btn-sm"
                                    data-bs-toggle="modal"
                                    data-bs-target="#modalRecarga"
                                    data-documento="<%= v.getNumeroDocumento() %>"
                                    data-nombre="<%= v.getNombreCompleto() != null ? v.getNombreCompleto() : v.getNumeroDocumento() %>">
                                💳 Recargar
                            </button>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>
    </div>
</div>

<!-- Modal de recarga -->
<div class="modal fade" id="modalRecarga" tabindex="-1" aria-labelledby="modalRecargaLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalRecargaLabel">💳 Recargar saldo</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/admin/visitors/recharge">
                <div class="modal-body">
                    <p class="text-muted mb-3">Visitante: <strong id="nombreVisitante"></strong></p>
                    <input type="hidden" name="documento" id="documentoInput">
                    <div class="mb-3">
                        <label for="amountInput" class="form-label">Monto a recargar ($)</label>
                        <input type="number" class="form-control" id="amountInput" name="amount"
                               min="1000" step="1000" placeholder="Ej: 50000" required>
                    </div>
                    <div class="d-flex flex-wrap gap-2">
                        <button type="button" class="btn btn-outline-success btn-sm preset-admin" data-amount="20000">+ $20.000</button>
                        <button type="button" class="btn btn-outline-success btn-sm preset-admin" data-amount="50000">+ $50.000</button>
                        <button type="button" class="btn btn-outline-success btn-sm preset-admin" data-amount="100000">+ $100.000</button>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success">Recargar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Pasar datos del visitante al modal de recarga
    var modalRecarga = document.getElementById('modalRecarga');
    modalRecarga.addEventListener('show.bs.modal', function(event) {
        var btn = event.relatedTarget;
        document.getElementById('documentoInput').value = btn.dataset.documento;
        document.getElementById('nombreVisitante').textContent = btn.dataset.nombre;
        document.getElementById('amountInput').value = '';
    });

    // Botones de monto rápido
    document.querySelectorAll('.preset-admin').forEach(function(btn) {
        btn.addEventListener('click', function() {
            document.getElementById('amountInput').value = this.dataset.amount;
        });
    });
</script>
</body>
</html>
