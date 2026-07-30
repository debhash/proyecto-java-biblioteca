<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Catálogo - Biblioteca Digital UNTEC</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Spectral:ital,wght@0,400;0,500;0,600;1,400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="container py-4">
    <section class="page-header d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
        <h2 class="m-0">Catálogo de libros</h2>
        <c:if test="${sessionScope.user != null && sessionScope.user.isLibrarian()}">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/books?action=new">Registrar libro</a>
        </c:if>
    </section>

    <c:choose>
        <c:when test="${param.success == 'created'}">
            <div class="alert alert-success" role="alert">El libro fue registrado correctamente.</div>
        </c:when>
        <c:when test="${param.success == 'updated'}">
            <div class="alert alert-success" role="alert">El libro fue actualizado correctamente.</div>
        </c:when>
        <c:when test="${param.success == 'deleted'}">
            <div class="alert alert-success" role="alert">El libro fue eliminado correctamente.</div>
        </c:when>
        <c:when test="${param.error == 'forbidden'}">
            <div class="alert alert-danger" role="alert">No tiene permisos para realizar esa acción.</div>
        </c:when>
    </c:choose>

    <c:choose>
        <c:when test="${empty books}">
            <div class="card border-dashed text-center p-5 text-muted">
                <p class="m-0">Aún no hay libros registrados en la biblioteca.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th>Titulo</th>
                            <th>Autor</th>
                            <th>ISBN</th>
                            <th>Disponibilidad</th>
                            <c:if test="${sessionScope.user != null && sessionScope.user.isLibrarian()}">
                                <th class="text-end">Acciones</th>
                            </c:if>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="book" items="${books}">
                            <tr>
                                <td data-label="Titulo"><c:out value="${book.title}"/></td>
                                <td data-label="Autor"><c:out value="${book.author}"/></td>
                                <td data-label="ISBN"><c:out value="${book.isbn}"/></td>
                                <td data-label="Disponibilidad">
                                    <c:choose>
                                        <c:when test="${book.available}">
                                            <span class="badge bg-success">Disponible</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Prestado</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <c:if test="${sessionScope.user != null && sessionScope.user.isLibrarian()}">
                                    <td data-label="Acciones" class="text-end">
                                        <div class="d-flex justify-content-end gap-2 flex-wrap">
                                            <a class="btn btn-outline-secondary btn-sm"
                                               href="${pageContext.request.contextPath}/books?action=edit&id=${book.id}">Editar</a>
                                            <form action="${pageContext.request.contextPath}/books" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${book.id}">
                                                <button type="submit" class="btn btn-danger btn-sm"
                                                        onclick="return confirm('¿Está seguro de eliminar este libro?');">
                                                    Eliminar
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script src="${pageContext.request.contextPath}/css/bootstrap/bootstrap.bundle.min.js"></script>
</body>
</html>
