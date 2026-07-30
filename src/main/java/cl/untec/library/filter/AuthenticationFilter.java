package cl.untec.library.filter;

import cl.untec.library.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Filtro para cuidar que nadie entre a rutas privadas sin haber iniciado sesión.
 * Si la ruta es pública, dejo pasar; si no, reviso la sesión antes de seguir.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

  /** Conjunto de rutas que no requieren autenticación previa. */
  private static final Set<String> PUBLIC_PATHS = Set.of(
    "/login",
    "/logout",
    "/",
    "/index.jsp"
  );

  /**
   * Reviso cada petición y decido si sigue su camino o si la devuelvo al login.
   *
   * @param req petición entrante del cliente.
   * @param res respuesta hacia el cliente.
   * @param chain cadena de filtros de Jakarta Servlet.
   * @throws IOException si ocurre un problema de entrada o salida.
   * @throws ServletException si falla el procesamiento del filtro.
   */
  @Override
  public void doFilter(
    ServletRequest req,
    ServletResponse res,
    FilterChain chain
  ) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String contextPath = request.getContextPath();
    String path = request.getRequestURI().substring(contextPath.length());

    if (isPublic(path)) {
      chain.doFilter(request, response);
      return;
    }

    HttpSession session = request.getSession(false);
    User user = session == null ? null : (User) session.getAttribute("user");
    if (user == null) {
      response.sendRedirect(contextPath + "/login");
      return;
    }

    chain.doFilter(request, response);
  }

  /**
   * Pregunto si una ruta puede abrirse sin sesión iniciada.
   *
   * @param path ruta solicitada por el cliente, sin el context path.
   * @return {@code true} si la ruta es pública.
   */
  private boolean isPublic(String path) {
    if (PUBLIC_PATHS.contains(path)) {
      return true;
    }
    // Recursos estáticos como CSS, JS o imágenes también deben poder cargarse sin iniciar sesión.
    return (
      path.startsWith("/css/") ||
      path.startsWith("/js/") ||
      path.startsWith("/images/")
    );
  }
}
