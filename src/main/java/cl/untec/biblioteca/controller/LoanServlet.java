package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.BookDAO;
import cl.untec.biblioteca.dao.LoanDAO;
import cl.untec.biblioteca.dao.UserDAO;
import cl.untec.biblioteca.model.Book;
import cl.untec.biblioteca.model.Loan;
import cl.untec.biblioteca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Servlet controlador del recurso {@code loan} (prestamos de libros).
 *
 * <p>
 * Mapeado a la URL {@code /loans} mediante la anotacion
 * {@link WebServlet}. Despacha las acciones recibidas como parametro
 * de la peticion:
 * </p>
 * <ul>
 *   <li>{@code list} (GET, bibliotecario): lista todos los prestamos
 *       registrados en el sistema.</li>
 *   <li>{@code new} (GET, bibliotecario): muestra el formulario de
 *       nuevo prestamo, con el catalogo de libros disponibles y la
 *       lista de estudiantes.</li>
 *   <li>{@code create} (POST, bibliotecario): registra un prestamo
 *       a partir de los datos del formulario.</li>
 *   <li>{@code return} (POST, bibliotecario): registra la devolucion
 *       de un prestamo existente.</li>
 *   <li>{@code my-loans} (GET, estudiante): lista los prestamos del
 *       estudiante autenticado.</li>
 * </ul>
 */
@WebServlet(name = "LoanServlet", urlPatterns = "/loans")
public class LoanServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** DAO responsable de las operaciones de persistencia de prestamos. */
    private final LoanDAO loanDAO = new LoanDAO();

    /** DAO utilizado para consultar libros disponibles al crear prestamos. */
    private final BookDAO bookDAO = new BookDAO();

    /** DAO utilizado para obtener la lista de estudiantes en el formulario. */
    private final UserDAO userDAO = new UserDAO();

    /**
     * Atiende las peticiones GET despachando segun la accion solicitada.
     *
     * <p>
     * Si no se indica ninguna accion, se asume {@code list}. Las
     * acciones validas son {@code list}, {@code new} y
     * {@code my-loans}; cualquier otra accion cae en el listado por
     * defecto.
     * </p>
     *
     * @param request  peticion HTTP recibida del cliente.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws ServletException si ocurre un error al reenviar la
     *                          peticion a una JSP.
     * @throws IOException      si ocurre un error de entrada o salida
     *                          al despachar la peticion.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "new":
                showForm(request, response);
                break;
            case "my-loans":
                myLoans(request, response);
                break;
            case "list":
            default:
                list(request, response);
                break;
        }
    }

    /**
     * Atiende las peticiones POST despachando la accion de escritura
     * correspondiente.
     *
     * <p>
     * Las acciones validas son {@code create} y {@code return}. Si
     * no se proporciona ninguna accion, o si la accion no es
     * reconocida, se redirige al listado principal.
     * </p>
     *
     * @param request  peticion HTTP recibida del cliente.
     * @param response respuesta HTTP que se enviara al cliente.
     * @throws ServletException si ocurre un error al reenviar la
     *                          peticion a una JSP.
     * @throws IOException      si ocurre un error de entrada o salida
     *                          al despachar o redirigir la peticion.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/loans?action=list");
            return;
        }
        switch (action) {
            case "create":
                create(request, response);
                break;
            case "return":
                registerReturn(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/loans?action=list");
                break;
        }
    }

    /**
     * Carga el listado completo de prestamos y lo reenvia a la vista
     * de administracion de prestamos.
     *
     * <p>
     * El usuario debe tener rol de bibliotecario; si no, se redirige
     * al catalogo y el metodo retorna sin cargar la lista.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        request.setAttribute("loans", loanDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/loans.jsp").forward(request, response);
    }

    /**
     * Muestra el formulario para registrar un nuevo prestamo.
     *
     * <p>
     * Carga unicamente los libros disponibles (filtrando los que
     * estan siendo prestados) y la lista de estudiantes. Requiere
     * que el usuario autenticado sea bibliotecario.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        List<Book> availableBooks = bookDAO.findAll().stream()
                .filter(Book::isAvailable)
                .toList();
        List<User> students = userDAO.findStudents();
        request.setAttribute("books", availableBooks);
        request.setAttribute("students", students);
        request.getRequestDispatcher("/WEB-INF/views/loan-form.jsp").forward(request, response);
    }

    /**
     * Procesa la creacion de un prestamo a partir de los datos del
     * formulario.
     *
     * <p>
     * Valida que se hayan seleccionado un estudiante y un libro, y
     * luego delega en {@link LoanDAO#registerLoan(Long, Long)} que
     * realiza la transaccion correspondiente. Si el libro no esta
     * disponible, se recarga el formulario mostrando un mensaje de
     * error. En caso de exito, se redirige al listado con un
     * indicador.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        String userIdParam = request.getParameter("userId");
        String bookIdParam = request.getParameter("bookId");

        Long userId = parseLong(userIdParam);
        Long bookId = parseLong(bookIdParam);

        if (userId == null || bookId == null) {
            reloadFormWithError(request, response,
                    "Debe seleccionar un estudiante y un libro.");
            return;
        }

        boolean ok = loanDAO.registerLoan(userId, bookId);
        if (!ok) {
            reloadFormWithError(request, response,
                    "El libro seleccionado no esta disponible.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/loans?action=list&success=created");
    }

    /**
     * Registra la devolucion de un prestamo existente.
     *
     * <p>
     * Identifica el prestamo a partir del parametro {@code id} y
     * delega en {@link LoanDAO#registerReturn(Long)} para aplicar la
     * devolucion. Si el identificador no es valido, o si el prestamo
     * ya habia sido devuelto, se redirige al listado con un parametro
     * de error adecuado.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws IOException si ocurre un error al enviar la redireccion.
     */
    private void registerReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        Long loanId = parseLong(request.getParameter("id"));
        if (loanId == null) {
            response.sendRedirect(request.getContextPath() + "/loans?action=list&error=invalid");
            return;
        }
        boolean ok = loanDAO.registerReturn(loanId);
        String query = ok ? "success=returned" : "error=alreadyReturned";
        response.sendRedirect(request.getContextPath() + "/loans?action=list&" + query);
    }

    /**
     * Muestra los prestamos del estudiante autenticado.
     *
     * <p>
     * Se obtiene el usuario desde la sesion. Si no hay sesion
     * activa, se redirige al formulario de login. En caso contrario,
     * se cargan sus prestamos y se reenvia a la vista
     * {@code my-loans.jsp}.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void myLoans(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        List<Loan> loans = loanDAO.findByUser(user.getId());
        request.setAttribute("loans", loans);
        request.getRequestDispatcher("/WEB-INF/views/my-loans.jsp").forward(request, response);
    }

    /**
     * Recarga el formulario de prestamo con un mensaje de error y
     * los datos necesarios para volver a renderizarlo.
     *
     * <p>
     * Se reutiliza para todos los casos en los que la creacion de un
     * prestamo falla tras la validacion inicial, evitando duplicar
     * la logica de carga de libros disponibles y estudiantes en
     * cada uno de ellos.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @param message  mensaje de error que se mostrara al usuario.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void reloadFormWithError(HttpServletRequest request, HttpServletResponse response,
                                     String message) throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        List<Book> availableBooks = bookDAO.findAll().stream()
                .filter(Book::isAvailable)
                .toList();
        List<User> students = userDAO.findStudents();
        request.setAttribute("books", availableBooks);
        request.setAttribute("students", students);
        request.getRequestDispatcher("/WEB-INF/views/loan-form.jsp").forward(request, response);
    }

    /**
     * Convierte una cadena en un {@link Long} de forma segura.
     *
     * <p>
     * Se utiliza para interpretar parametros {@code id} de la URL
     * sin que una entrada malformada provoque una excepcion. Si el
     * valor es {@code null} o no puede convertirse, se retorna
     * {@code null} y el caller decide como manejar la situacion
     * (típicamente redirigiendo).
     * </p>
     *
     * @param value cadena a convertir.
     * @return el valor como {@link Long} o {@code null} si no es
     *         un numero valido o si la cadena es nula.
     */
    private Long parseLong(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Verifica que el usuario autenticado tenga rol de bibliotecario.
     *
     * <p>
     * Si no hay sesion, o si el usuario no es bibliotecario, se
     * redirige al catalogo con un parametro {@code error=forbidden}
     * y se retorna {@code false} para que el caller aborte la
     * operacion. Este metodo centraliza la comprobacion de rol
     * para todas las acciones administrativas de prestamos.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @return {@code true} si el usuario es bibliotecario y puede
     *         continuar; {@code false} si fue redirigido.
     * @throws IOException si ocurre un error al enviar la redireccion.
     */
    private boolean isLibrarian(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || !user.isLibrarian()) {
            response.sendRedirect(request.getContextPath() + "/books?action=list&error=forbidden");
            return false;
        }
        return true;
    }
}
