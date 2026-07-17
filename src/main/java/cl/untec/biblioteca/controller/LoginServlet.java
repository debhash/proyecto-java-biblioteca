package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.UserDAO;
import cl.untec.biblioteca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Servlet encargado de autenticar a los usuarios de la aplicacion.
 *
 * <p>
 * Mapeado a la URL {@code /login} mediante la anotacion
 * {@link WebServlet}. Soporta dos verbos HTTP:
 * </p>
 * <ul>
 *   <li>{@code GET}: muestra el formulario de inicio de sesion.
 *       Si el usuario ya tiene una sesion activa, se redirige
 *       directamente al catalogo para evitar mostrar el login
 *       innecesariamente.</li>
 *   <li>{@code POST}: valida las credenciales ingresadas contra
 *       la base de datos. Si son correctas, guarda al usuario en
 *       la sesion y lo redirige al catalogo. En caso contrario,
 *       vuelve a mostrar el formulario con un mensaje de error.</li>
 * </ul>
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** DAO utilizado para consultar usuarios en la base de datos. */
    private final UserDAO userDAO = new UserDAO();

    /**
     * Atiende las peticiones GET mostrando el formulario de login o
     * redirigiendo al catalogo si ya existe una sesion activa.
     *
     * <p>
     * Se consulta la sesion sin crearla ({@code getSession(false)})
     * y se revisa el atributo {@code "user"}. Si esta presente, se
     * evita volver a renderizar el login y se envia al usuario al
     * listado de libros. En caso contrario, se reenvia la peticion
     * a la JSP {@code login.jsp}.
     * </p>
     *
     * @param request  peticion HTTP recibida del cliente.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws ServletException si ocurre un error al reenviar la
     *                          peticion a la JSP de login.
     * @throws IOException      si ocurre un error de entrada o salida
     *                          al redirigir o al reenviar la peticion.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si ya hay una sesion activa, se redirige al catalogo.
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/books?action=list");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    /**
     * Atiende las peticiones POST validando las credenciales del
     * usuario e iniciando la sesion si son correctas.
     *
     * <p>
     * El flujo es el siguiente:
     * </p>
     * <ol>
     *   <li>Se leen los parametros {@code email} y {@code password}.
     *       Si alguno viene vacio, se vuelve a mostrar el formulario
     *       con un mensaje de error.</li>
     *   <li>Se consulta al {@link UserDAO} para validar las
     *       credenciales. Si no coinciden, se muestra un mensaje
     *       generico de "credenciales incorrectas" y se conserva
     *       el email ingresado para evitar que el usuario lo
     *       reescriba.</li>
     *   <li>Si las credenciales son validas, se guarda al usuario
     *       en la sesion, se configura un tiempo maximo de
     *       inactividad de 30 minutos y se redirige al catalogo.</li>
     * </ol>
     *
     * @param request  peticion HTTP con los parametros del formulario.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws ServletException si ocurre un error al reenviar la
     *                          peticion a la JSP de login.
     * @throws IOException      si ocurre un error de entrada o salida
     *                          al redirigir o al reenviar la peticion.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validaciones basicas del lado servidor.
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errorMessage", "Debe ingresar email y contrasena.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Optional<User> userOpt = userDAO.findByEmailAndPassword(email.trim(), password);
        if (userOpt.isEmpty()) {
            request.setAttribute("errorMessage", "Credenciales incorrectas.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        User user = userOpt.get();
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30 * 60); // 30 minutos de inactividad.

        response.sendRedirect(request.getContextPath() + "/books?action=list");
    }
}
