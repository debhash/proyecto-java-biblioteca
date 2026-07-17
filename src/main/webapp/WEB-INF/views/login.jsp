<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesion - Biblioteca Digital UNTEC</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Spectral:ital,wght@0,400;0,500;0,600;1,400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<main class="login-page">
    <section class="card login-card shadow">
        <div class="card-body">
            <h1 class="h4 text-center mb-2">Biblioteca Digital UNTEC</h1>
            <p class="text-center text-muted mb-4">Ingrese sus credenciales para continuar.</p>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post" novalidate>
                <div class="mb-3">
                    <label for="email" class="form-label fw-semibold">Email</label>
                    <input type="email" class="form-control" id="email" name="email"
                           value="<c:out value='${email}'/>" required>
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label fw-semibold">Contrasena</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Ingresar</button>
            </form>

            <div class="help-text mt-4">
                <p class="fw-semibold mb-1">Usuarios de prueba:</p>
                <ul class="mb-0 ps-3">
                    <li>Bibliotecario: <code>luis@untec.cl</code> / <code>admin123</code></li>
                    <li>Estudiante: <code>bruce@untec.cl</code> / <code>estudiante123</code></li>
                </ul>
            </div>
        </div>
    </section>
</main>
</body>
</html>
