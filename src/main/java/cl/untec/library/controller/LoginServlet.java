package cl.untec.library.controller;

import cl.untec.library.dao.UserDAO;
import cl.untec.library.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Servlet que maneja el inicio de sesión.
 * Recibe los datos del formulario, revisa si calzan y guarda al usuario en la sesión cuando todo está bien.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private final UserDAO userDAO = new UserDAO();

  /**
   * Muestra el formulario de inicio de sesión.
   *
   * @param request petición HTTP entrante.
   * @param response respuesta HTTP hacia el navegador.
   * @throws ServletException si ocurre un error al reenviar a la vista.
   * @throws IOException si falla la comunicación con el cliente.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    request
      .getRequestDispatcher("/WEB-INF/views/login.jsp")
      .forward(request, response);
  }

  /**
   * Procesa el formulario de login y autentica al usuario contra la base de datos.
   * Si las credenciales son válidas, guarda al usuario en sesión y lo manda al catálogo.
   *
   * @param request petición HTTP con email y contraseña.
   * @param response respuesta HTTP hacia el navegador.
   * @throws ServletException si ocurre un error al reenviar la vista.
   * @throws IOException si falla la redirección o el envío de respuesta.
   */
  @Override
  protected void doPost(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    String email = request.getParameter("email");
    char[] passwordChars =
      request.getParameter("password") == null
        ? new char[0]
        : request.getParameter("password").toCharArray();

    try {
      Optional<User> user = userDAO.findByEmailAndPassword(
        email,
        passwordChars
      );
      if (user.isEmpty()) {
        request.setAttribute(
          "errorMessage",
          "Credenciales invalidas. Intente nuevamente."
        );
        request.setAttribute("email", email);
        request
          .getRequestDispatcher("/WEB-INF/views/login.jsp")
          .forward(request, response);
        return;
      }

      HttpSession session = request.getSession(true);
      session.setAttribute("user", user.get());
      response.sendRedirect(request.getContextPath() + "/books?action=list");
    } finally {
      Arrays.fill(passwordChars, '\0');
    }
  }
}
