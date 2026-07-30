<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registrar prestamo - Biblioteca Digital UNTEC</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Spectral:ital,wght@0,400;0,500;0,600;1,400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="container py-4">
    <section class="page-header mb-4">
        <h2 class="m-0">Registrar préstamo</h2>
    </section>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <form class="card shadow-sm mx-auto form-card" action="${pageContext.request.contextPath}/loans" method="post" novalidate>
        <div class="card-body p-4">
            <input type="hidden" name="action" value="create">

            <div class="mb-3">
                <label for="userId" class="form-label fw-semibold">Estudiante *</label>
                <select class="form-select" id="userId" name="userId" required>
                    <option value="">Seleccione un estudiante</option>
                    <c:forEach var="student" items="${students}">
                        <option value="${student.id}">
                            <c:out value="${student.name}"/> (<c:out value="${student.email}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-4">
                <label for="bookId" class="form-label fw-semibold">Libro disponible *</label>
                <select class="form-select" id="bookId" name="bookId" required>
                    <option value="">Seleccione un libro</option>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.id}">
                            <c:out value="${book.title}"/> - <c:out value="${book.author}"/>
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${empty books}">
                    <small class="text-muted">No hay libros disponibles para prestar en este momento.</small>
                </c:if>
            </div>

            <div class="d-flex justify-content-end gap-2">
                <button type="submit" class="btn btn-primary"
                        <c:if test="${empty books}">disabled</c:if>>
                    Registrar préstamo
                </button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/loans?action=list">Cancelar</a>
            </div>
        </div>
    </form>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script src="${pageContext.request.contextPath}/css/bootstrap/bootstrap.bundle.min.js"></script>
</body>
</html>
