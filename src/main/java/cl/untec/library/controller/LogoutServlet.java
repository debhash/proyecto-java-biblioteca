package cl.untec.library.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet responsable de cerrar la sesión del usuario.
 * Invalida la sesión activa y devuelve a la pantalla de inicio de sesión.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

  /**
   * Invalida la sesión actual y redirige al login.
   *
   * @param request petición HTTP.
   * @param response respuesta HTTP.
   * @throws IOException si ocurre un error al redirigir.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    response.sendRedirect(request.getContextPath() + "/login");
  }
}
