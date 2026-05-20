<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tech-Park UQ — Registro</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="container d-flex justify-content-center align-items-center min-vh-100 py-4">
    <div class="card shadow" style="max-width: 480px; width: 100%;">
        <div class="card-body p-4">
            <h4 class="card-title text-center mb-1">🎢 Tech-Park UQ</h4>
            <p class="text-center text-muted small mb-4">Crea tu cuenta de visitante</p>

            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
            <div class="alert alert-danger"><%= error %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/visitor/register" enctype="multipart/form-data">
                <div class="mb-3">
                    <label class="form-label">Nombre completo <span class="text-danger">*</span></label>
                    <input type="text" name="fullName" class="form-control"
                           placeholder="Ej: Ana Torres" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Número de documento <span class="text-danger">*</span></label>
                    <input type="text" name="documentNumber" class="form-control"
                           placeholder="Ej: 12345678" required>
                    <div class="form-text">Este será tu usuario para iniciar sesión.</div>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-6">
                        <label class="form-label">Edad <span class="text-danger">*</span></label>
                        <input type="number" name="age" class="form-control" min="1" max="120"
                               placeholder="Ej: 25" required>
                    </div>
                    <div class="col-6">
                        <label class="form-label">Estatura (cm) <span class="text-danger">*</span></label>
                        <input type="number" name="heightCm" class="form-control" min="50" max="250"
                               placeholder="Ej: 170" required>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contraseña <span class="text-danger">*</span></label>
                    <input type="password" name="password" class="form-control"
                           placeholder="Mínimo 6 caracteres" required minlength="6">
                </div>
                <div class="mb-3">
                    <label class="form-label">Saldo virtual inicial ($)</label>
                    <input type="number" name="virtualBalance" class="form-control"
                           value="50000" min="0" step="1000">
                    <div class="form-text">Saldo para usar en atracciones con costo adicional.</div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Foto de perfil (opcional)</label>
                    <input type="file" name="photo" class="form-control" accept="image/*">
                </div>
                <button type="submit" class="btn btn-primary w-100">Crear cuenta</button>
            </form>
            <hr>
            <p class="text-center text-muted small mb-0">
                ¿Ya tienes cuenta?
                <a href="${pageContext.request.contextPath}/login.jsp">Inicia sesión</a>
            </p>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
