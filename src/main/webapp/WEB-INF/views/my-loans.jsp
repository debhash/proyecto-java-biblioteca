<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis prestamos - Biblioteca Digital UNTEC</title>
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
        <h2 class="m-0">Mis prestamos</h2>
    </section>

    <c:choose>
        <c:when test="${empty loans}">
            <div class="card text-center p-5">
                <p class="text-muted mb-3">Aun no tienes prestamos registrados.</p>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/books?action=list">
                    Explorar catalogo
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th>Libro</th>
                            <th>Fecha prestamo</th>
                            <th>Fecha devolucion</th>
                            <th>Estado</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="loan" items="${loans}">
                            <tr>
                                <td data-label="Libro"><c:out value="${loan.bookTitle}"/></td>
                                <td data-label="Fecha prestamo">${loan.loanDate}</td>
                                <td data-label="Fecha devolucion">
                                    <c:choose>
                                        <c:when test="${loan.returnDate != null}">
                                            ${loan.returnDate}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Pendiente</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td data-label="Estado">
                                    <c:choose>
                                        <c:when test="${loan.returned}">
                                            <span class="badge bg-success">Devuelto</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Activo</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
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
