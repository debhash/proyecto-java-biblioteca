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
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private final UserDAO userDAO = new UserDAO();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    request
      .getRequestDispatcher("/WEB-INF/views/login.jsp")
      .forward(request, response);
  }

  @Override
  protected void doPost(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    Optional<User> user = userDAO.findByEmailAndPassword(email, password);
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
  }
}
