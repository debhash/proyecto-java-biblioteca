package cl.untec.biblioteca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet encargado de cerrar la sesion del usuario.
 *
 * <p>
 * Mapeado a la URL {@code /logout} mediante la anotacion
 * {@link WebServlet}. Acepta tanto {@code GET} como {@code POST}
 * para permitir enlaces directos en la navegacion y formularios
 * que prefieran un cierre de sesion por POST (mas seguro frente
 * a preflight CSRF o a enlaces embebidos en otros sitios).
 * </p>
 * <p>
 * Tras invalidar la sesion, el usuario siempre es redirigido al
 * formulario de login.
 * </p>
 */
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Atiende las peticiones GET cerrando la sesion actual y
     * redirigiendo al formulario de login.
     *
     * @param request  peticion HTTP recibida del cliente.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws ServletException si ocurre un error en el procesamiento
     *                          del servlet.
     * @throws IOException      si ocurre un error de entrada o salida
     *                          al redirigir al login.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }

    /**
     * Atiende las peticiones POST cerrando la sesion actual y
     * redirigiendo al formulario de login.
     *
     * @param request  peticion HTTP recibida del cliente.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws ServletException si ocurre un error en el procesamiento
     *                          del servlet.
     * @throws IOException      si ocurre un error de entrada o salida
     *                          al redirigir al login.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }

    /**
     * Invalida la sesion actual del usuario, si existe, y lo
     * redirige al formulario de login.
     *
     * <p>
     * Se utiliza {@code getSession(false)} para evitar crear una
     * sesion nueva en el caso de que el usuario ya no tenga una
     * abierta. Luego, si la sesion existe, se invalida. Finalmente,
     * se redirige a la pantalla de login independiente del resultado
     * de las operaciones anteriores.
     * </p>
     *
     * @param request  peticion HTTP recibida del cliente.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws IOException si ocurre un error al enviar la redireccion.
     */
    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
