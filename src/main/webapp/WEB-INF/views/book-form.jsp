<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Formulario de libro - Biblioteca Digital UNTEC</title>
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
        <h2 class="m-0">
            <c:choose>
                <c:when test="${isEdit}">Editar libro</c:when>
                <c:otherwise>Registrar nuevo libro</c:otherwise>
            </c:choose>
        </h2>
    </section>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <form class="card shadow-sm mx-auto form-card" action="${pageContext.request.contextPath}/books" method="post" novalidate>
        <div class="card-body p-4">
            <input type="hidden" name="action" value="<c:out value='${isEdit ? "update" : "create"}'/>">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${book.id}">
            </c:if>

            <div class="mb-3">
                <label for="title" class="form-label fw-semibold">Titulo *</label>
                <input type="text" class="form-control" id="title" name="title"
                       value="<c:out value='${book.title}'/>" required>
            </div>
            <div class="mb-3">
                <label for="author" class="form-label fw-semibold">Autor *</label>
                <input type="text" class="form-control" id="author" name="author"
                       value="<c:out value='${book.author}'/>" required>
            </div>
            <div class="mb-3">
                <label for="isbn" class="form-label fw-semibold">ISBN *</label>
                <input type="text" class="form-control" id="isbn" name="isbn"
                       value="<c:out value='${book.isbn}'/>" required>
            </div>
            <div class="mb-4 form-check">
                <input type="checkbox" class="form-check-input" id="available" name="available" value="true"
                       <c:if test="${book.available || !isEdit}">checked</c:if>>
                <label class="form-check-label" for="available">Disponible para préstamo</label>
            </div>

            <div class="d-flex justify-content-end gap-2">
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${isEdit}">Guardar cambios</c:when>
                        <c:otherwise>Registrar libro</c:otherwise>
                    </c:choose>
                </button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/books?action=list">Cancelar</a>
            </div>
        </div>
    </form>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script src="${pageContext.request.contextPath}/css/bootstrap/bootstrap.bundle.min.js"></script>
</body>
</html>
