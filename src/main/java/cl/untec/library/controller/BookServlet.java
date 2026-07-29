package cl.untec.library.controller;

import cl.untec.library.dao.BookDAO;
import cl.untec.library.model.Book;
import cl.untec.library.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet encargado de gestionar el catálogo de libros.
 * Permite listar, crear, editar y eliminar libros, validando que solo el bibliotecario pueda modificar datos.
 */
@WebServlet("/books")
public class BookServlet extends HttpServlet {

  private final BookDAO bookDAO = new BookDAO();

  /**
   * Atiende peticiones GET para listar libros o mostrar el formulario de creación/edición.
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

    String action = request.getParameter("action");
    if (action == null || action.isBlank() || "list".equals(action)) {
      request.setAttribute("books", bookDAO.findAll());
      request
        .getRequestDispatcher("/WEB-INF/views/books.jsp")
        .forward(request, response);
      return;
    }

    User user = (User) session.getAttribute("user");
    if ("new".equals(action)) {
      if (!user.isLibrarian()) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=forbidden"
        );
        return;
      }
      request.setAttribute("book", new Book());
      request.setAttribute("isEdit", false);
      request
        .getRequestDispatcher("/WEB-INF/views/book-form.jsp")
        .forward(request, response);
      return;
    }

    if ("edit".equals(action)) {
      if (!user.isLibrarian()) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=forbidden"
        );
        return;
      }

      Long id = parseLong(request.getParameter("id"));
      if (id == null) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=invalid"
        );
        return;
      }

      Book book = bookDAO.findById(id).orElse(null);
      if (book == null) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=invalid"
        );
        return;
      }

      request.setAttribute("book", book);
      request.setAttribute("isEdit", true);
      request
        .getRequestDispatcher("/WEB-INF/views/book-form.jsp")
        .forward(request, response);
      return;
    }

    response.sendRedirect(request.getContextPath() + "/books?action=list");
  }

  /**
   * Atiende peticiones POST para crear, actualizar o eliminar libros.
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
    if (!user.isLibrarian()) {
      response.sendRedirect(
        request.getContextPath() + "/books?action=list&error=forbidden"
      );
      return;
    }

    String action = request.getParameter("action");
    if ("create".equals(action)) {
      Book book = readBookForm(request);
      bookDAO.create(book);
      response.sendRedirect(
        request.getContextPath() + "/books?action=list&success=created"
      );
      return;
    }

    if ("update".equals(action)) {
      Long id = parseLong(request.getParameter("id"));
      if (id == null) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=invalid"
        );
        return;
      }

      Book book = readBookForm(request);
      book.setId(id);
      boolean updated = bookDAO.update(book);
      if (updated) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&success=updated"
        );
      } else {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=invalid"
        );
      }
      return;
    }

    if ("delete".equals(action)) {
      Long id = parseLong(request.getParameter("id"));
      if (id == null) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=invalid"
        );
        return;
      }

      boolean deleted = bookDAO.delete(id);
      if (deleted) {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&success=deleted"
        );
      } else {
        response.sendRedirect(
          request.getContextPath() + "/books?action=list&error=invalid"
        );
      }
    }
  }

  /**
   * Construye un objeto Book a partir de los campos del formulario.
   *
   * @param request petición HTTP con los datos del formulario.
   * @return instancia de Book lista para persistirse.
   */
  private Book readBookForm(HttpServletRequest request) {
    Book book = new Book();
    book.setTitle(request.getParameter("title"));
    book.setAuthor(request.getParameter("author"));
    book.setIsbn(request.getParameter("isbn"));
    book.setAvailable(request.getParameter("available") != null);
    return book;
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
   * Convierte un valor textual a Long de forma segura.
   *
   * @param value valor recibido por parámetro.
   * @return número convertido o {@code null} si el dato no es válido.
   */
  private Long parseLong(String value) {
    try {
      return value == null || value.isBlank() ? null : Long.parseLong(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
