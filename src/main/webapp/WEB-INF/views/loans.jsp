<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Prestamos - Biblioteca Digital UNTEC</title>
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
        <h2 class="m-0">Listado de préstamos</h2>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/loans?action=new">Registrar prestamo</a>
    </section>

    <c:choose>
        <c:when test="${param.success == 'created'}">
            <div class="alert alert-success" role="alert">El préstamo fue registrado correctamente.</div>
        </c:when>
        <c:when test="${param.success == 'returned'}">
            <div class="alert alert-success" role="alert">La devolución fue registrada correctamente.</div>
        </c:when>
        <c:when test="${param.error == 'alreadyReturned'}">
            <div class="alert alert-danger" role="alert">El préstamo ya se encontraba devuelto.</div>
        </c:when>
        <c:when test="${param.error == 'invalid'}">
            <div class="alert alert-danger" role="alert">Solicitud inválida.</div>
        </c:when>
    </c:choose>

    <c:choose>
        <c:when test="${empty loans}">
            <div class="card text-center p-5 text-muted">
                <p class="m-0">Aún no se han registrado préstamos.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Estudiante</th>
                            <th>Libro</th>
                            <th>Fecha préstamo</th>
                            <th>Fecha devolución</th>
                            <th>Estado</th>
                            <th class="text-end">Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="loan" items="${loans}">
                            <tr>
                                <td data-label="ID">${loan.id}</td>
                                <td data-label="Estudiante"><c:out value="${loan.userName}"/></td>
                                <td data-label="Libro"><c:out value="${loan.bookTitle}"/></td>
                                <td data-label="Fecha préstamo">${loan.loanDate}</td>
                                <td data-label="Fecha devolución">
                                    <c:choose>
                                        <c:when test="${loan.returnDate != null}">
                                            ${loan.returnDate}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">-</span>
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
                                <td data-label="Acciones" class="text-end">
                                    <c:choose>
                                        <c:when test="${loan.returned}">
                                            <span class="text-muted small">Sin acciones</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/loans" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="return">
                                                <input type="hidden" name="id" value="${loan.id}">
                                                <button type="submit" class="btn btn-primary btn-sm">Registrar devolucion</button>
                                            </form>
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
