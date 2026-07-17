package cl.untec.biblioteca.filter;

import cl.untec.biblioteca.model.User;
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
 * Filtro de autenticacion que protege todas las URLs de la aplicacion
 * que requieren una sesion iniciada.
 *
 * <p>
 * Se aplica a cualquier peticion entrante gracias a la anotacion
 * {@link WebFilter} con patron {@code "/*"}. Si el usuario no esta
 * autenticado, la peticion se redirige al formulario de login.
 * Las URLs publicas (login, logout, raiz y recursos estaticos) se
 * dejan pasar sin verificar sesion. La autorizacion por rol se
 * valida ademas en cada servlet administrativo.
 * </p>
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    /** Conjunto de rutas que no requieren autenticacion previa. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login", "/logout", "/", "/index.jsp");

    /**
     * Intercepta cada peticion HTTP y decide si debe continuar la cadena
     * de filtros o redirigir al login.
     *
     * <p>
     * Si la ruta solicitada es publica o corresponde a un recurso
     * estatico, se delega al siguiente eslabon de la cadena. En caso
     * contrario, se obtiene la sesion actual y se busca el atributo
     * {@code "user"}; si no existe, se redirige al formulario de login.
     * Si la sesion es valida, la peticion continua normalmente.
     * </p>
     *
     * @param req   peticion entrante del cliente (se convierte a
     *              {@link HttpServletRequest} internamente).
     * @param res   respuesta hacia el cliente (se convierte a
     *              {@link HttpServletResponse} internamente).
     * @param chain cadena de filtros de Jakarta Servlet.
     * @throws IOException      si ocurre un error de entrada o salida al
     *                          reenviar la peticion o al redirigir al login.
     * @throws ServletException si ocurre un error en el procesamiento
     *                          de la cadena de filtros.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
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
     * Determina si una ruta es de acceso publico y no requiere
     * autenticacion.
     *
     * <p>
     * Se considera publica toda ruta presente en {@link #PUBLIC_PATHS}
     * o cualquier ruta que corresponda a un recurso estatico dentro de
     * los directorios {@code /css/}, {@code /js/} o {@code /images/}.
     * </p>
     *
     * @param path ruta solicitada por el cliente, sin el context path.
     * @return {@code true} si la ruta es publica y se puede omitir la
     *         validacion de sesion; {@code false} en caso contrario.
     */
    private boolean isPublic(String path) {
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        // Recursos estaticos servidos desde /css, /js, /images, etc.
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/");
    }
}
