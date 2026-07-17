<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="main-header">
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <h1 class="navbar-brand m-0 fs-5">
                <a href="${pageContext.request.contextPath}/books?action=list" class="text-white text-decoration-none">
                    Biblioteca Digital UNTEC
                </a>
            </h1>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#mainNavbar" aria-controls="mainNavbar"
                    aria-expanded="false" aria-label="Alternar navegacion">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="mainNavbar">
                <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/books?action=list">Catalogo</a>
                    </li>
                    <c:if test="${sessionScope.user != null && sessionScope.user.isLibrarian()}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/books?action=new">Nuevo libro</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/loans?action=list">Prestamos</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/loans?action=new">Registrar prestamo</a>
                        </li>
                    </c:if>
                    <c:if test="${sessionScope.user != null && !sessionScope.user.isLibrarian()}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/loans?action=my-loans">Mis prestamos</a>
                        </li>
                    </c:if>
                </ul>
                <div class="d-flex align-items-center gap-2 ms-auto user-area">
                    <c:choose>
                        <c:when test="${sessionScope.user != null}">
                            <span class="user-name">
                                <c:out value="${sessionScope.user.name}"/> (${sessionScope.user.roleLabel})
                            </span>
                            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/login">Iniciar sesion</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>
</header>
