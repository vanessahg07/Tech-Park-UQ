<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.model.ContextoDelParque" %>
<%
    // Si ya hay sesión activa, redirigir al panel correspondiente
    String rol = (String) (session != null ? session.getAttribute("role") : null);
    if ("ADMIN".equals(rol)) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    } else if ("OPERATOR".equals(rol)) {
        response.sendRedirect(request.getContextPath() + "/operator/panel");
        return;
    } else if ("VISITOR".equals(rol)) {
        response.sendRedirect(request.getContextPath() + "/visitor/profile");
        return;
    }
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Iniciar sesión</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body class="bg-light">
<div class="container d-flex justify-content-center align-items-center min-vh-100 py-4">
    <div class="card shadow" style="max-width: 420px; width: 100%;">
        <div class="card-body p-4">
            <h4 class="card-title text-center mb-1">🎢 Tech-Park UQ</h4>
            <p class="text-center text-muted small mb-4">Inicia sesión en tu cuenta</p>

            <% if (error != null) { %>
            <div class="alert alert-danger"><%= error %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/auth">
                <div class="mb-3">
                    <label class="form-label">Usuario (número de documento)</label>
                    <input type="text" name="username" class="form-control"
                           placeholder="Ej: admin" required autofocus>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" name="password" class="form-control"
                           placeholder="Tu contraseña" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Iniciar sesión</button>
            </form>
            <hr>
            <p class="text-center text-muted small mb-0">
                ¿No tienes cuenta?
                <a href="${pageContext.request.contextPath}/visitor/register">Regístrate aquí</a>
            </p>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
