<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<footer class="main-footer">
    <div class="container">
        <div class="row g-4">
            <div class="col-12 col-md-5">
                <h4>Biblioteca Digital UNTEC</h4>
                <p class="m-0">Sistema para la gestión y préstamo de libros de la biblioteca digital.</p>
            </div>
            <div class="col-6 col-md-3">
                <h4>Navegacion</h4>
                <ul class="list-unstyled m-0">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/books?action=list">Catálogo</a></li>
                    <c:if test="${sessionScope.user != null && sessionScope.user.isLibrarian()}">
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/loans?action=list">Préstamos</a></li>
                    </c:if>
                    <li><a href="${pageContext.request.contextPath}/login">Iniciar sesión</a></li>
                </ul>
            </div>
            <div class="col-6 col-md-4">
                <h4>Contacto</h4>
                <ul class="list-unstyled m-0">
                    <li class="mb-2">biblioteca@untec.cl</li>
                    <li class="mb-2">Av. Quiero Leer 1234, Santiago</li>
                    <li>Lunes a viernes, 9:00 a 18:00</li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom text-center">
            &copy; Biblioteca Digital UNTEC. Todos los derechos reservados.
        </div>
    </div>
</footer>
