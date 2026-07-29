package cl.untec.library.controller;

import cl.untec.library.dao.BookDAO;
import cl.untec.library.dao.LoanDAO;
import cl.untec.library.dao.UserDAO;
import cl.untec.library.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet encargado de gestionar préstamos y devoluciones.
 * El bibliotecario puede listar todos los préstamos, registrar uno nuevo y marcar devoluciones.
 * El estudiante solo puede ver sus propios préstamos.
 */
@WebServlet("/loans")
public class LoanServlet extends HttpServlet {

  private final LoanDAO loanDAO = new LoanDAO();
  private final UserDAO userDAO = new UserDAO();
  private final BookDAO bookDAO = new BookDAO();

  /**
   * Atiende peticiones GET para listar préstamos o mostrar el formulario de creación.
   *
   * @param request petición HTTP.
   * @param response respuesta HTTP.
   * @throws ServletException si falla el despacho a la vista.
   * @throws IOException si ocurre un problema de comunicación.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (!isAuthenticated(session)) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }

    User user = (User) session.getAttribute("user");
    String action = request.getParameter("action");

    if (action == null || action.isBlank() || "list".equals(action)) {
      if (user.isLibrarian()) {
        request.setAttribute("loans", loanDAO.findAll());
        request
          .getRequestDispatcher("/WEB-INF/views/loans.jsp")
          .forward(request, response);
      } else {
        request.setAttribute("loans", loanDAO.findByUser(user.getId()));
        request
          .getRequestDispatcher("/WEB-INF/views/my-loans.jsp")
          .forward(request, response);
      }
      return;
    }

    if ("my-loans".equals(action)) {
      request.setAttribute("loans", loanDAO.findByUser(user.getId()));
      request
        .getRequestDispatcher("/WEB-INF/views/my-loans.jsp")
        .forward(request, response);
      return;
    }

    if ("new".equals(action)) {
      if (!user.isLibrarian()) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&error=forbidden"
        );
        return;
      }
      request.setAttribute("students", userDAO.findStudents());
      request.setAttribute("books", bookDAO.findAvailable());
      request
        .getRequestDispatcher("/WEB-INF/views/loan-form.jsp")
        .forward(request, response);
      return;
    }

    response.sendRedirect(request.getContextPath() + "/loans?action=list");
  }

  /**
   * Atiende peticiones POST para registrar préstamos y devoluciones.
   *
   * @param request petición HTTP con los datos del formulario.
   * @param response respuesta HTTP.
   * @throws IOException si falla la redirección.
   */
  @Override
  protected void doPost(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException {
    HttpSession session = request.getSession(false);
    if (!isAuthenticated(session)) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }

    User user = (User) session.getAttribute("user");
    String action = request.getParameter("action");

    if ("create".equals(action)) {
      if (!user.isLibrarian()) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&error=forbidden"
        );
        return;
      }

      Long userId = parseLong(request.getParameter("userId"));
      Long bookId = parseLong(request.getParameter("bookId"));
      if (userId == null || bookId == null) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=new&error=invalid"
        );
        return;
      }

      boolean created = loanDAO.registerLoan(userId, bookId);
      if (created) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&success=created"
        );
      } else {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=new&error=invalid"
        );
      }
      return;
    }

    if ("return".equals(action)) {
      if (!user.isLibrarian()) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&error=forbidden"
        );
        return;
      }

      Long loanId = parseLong(request.getParameter("id"));
      if (loanId == null) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&error=invalid"
        );
        return;
      }

      boolean returned = loanDAO.registerReturn(loanId);
      if (returned) {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&success=returned"
        );
      } else {
        response.sendRedirect(
          request.getContextPath() + "/loans?action=list&error=alreadyReturned"
        );
      }
    }
  }

  /**
   * Verifica si la sesión actual tiene un usuario autenticado.
   *
   * @param session sesión HTTP activa.
   * @return {@code true} si hay usuario en sesión.
   */
  private boolean isAuthenticated(HttpSession session) {
    return session != null && session.getAttribute("user") != null;
  }

  /**
   * Convierte un parámetro textual a Long de forma segura.
   *
   * @param value valor recibido por parámetro.
   * @return número convertido o {@code null} si no se puede parsear.
   */
  private Long parseLong(String value) {
    try {
      return value == null || value.isBlank() ? null : Long.parseLong(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
