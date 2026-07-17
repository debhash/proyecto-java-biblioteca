package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.BookDAO;
import cl.untec.biblioteca.model.Book;
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
 * Servlet controlador del recurso {@code book} (catalogo de libros).
 *
 * <p>
 * Mapeado a la URL {@code /books} mediante la anotacion
 * {@link WebServlet}. Despacha las acciones recibidas como parametro
 * de la peticion:
 * </p>
 * <ul>
 *   <li>{@code list} (GET): muestra el catalogo completo de libros.</li>
 *   <li>{@code new} (GET): muestra el formulario de creacion.</li>
 *   <li>{@code create} (POST): persiste un libro nuevo.</li>
 *   <li>{@code edit} (GET): muestra el formulario con los datos de un
 *       libro existente.</li>
 *   <li>{@code update} (POST): guarda los cambios de un libro existente.</li>
 *   <li>{@code delete} (POST): elimina un libro existente.</li>
 * </ul>
 * <p>
 * Las acciones administrativas (crear, editar, actualizar, eliminar)
 * solo estan disponibles para usuarios con rol {@code LIBRARIAN}.
 * La validacion se realiza tambien en el servidor y no solo
 * ocultando los botones en las JSP, para evitar accesos indebidos
 * aun cuando la vista haya sido manipulada.
 * </p>
 */
@WebServlet(name = "BookServlet", urlPatterns = "/books")
public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** DAO encargado de las operaciones de persistencia de libros. */
    private final BookDAO bookDAO = new BookDAO();

    /**
     * Atiende las peticiones GET despachando segun la accion solicitada.
     *
     * <p>
     * Si no se indica ninguna accion, se asume {@code list}. Las
     * acciones {@code list}, {@code new} y {@code edit} se procesan
     * reenviando a la vista correspondiente. Cualquier accion
     * desconocida cae en el listado por defecto.
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
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
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
     * Las acciones validas son {@code create}, {@code update} y
     * {@code delete}. Si no se proporciona ninguna accion, o si la
     * accion no es reconocida, se redirige al listado para evitar
     * estados inconsistentes.
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
            response.sendRedirect(request.getContextPath() + "/books?action=list");
            return;
        }
        switch (action) {
            case "create":
                create(request, response);
                break;
            case "update":
                update(request, response);
                break;
            case "delete":
                delete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/books?action=list");
                break;
        }
    }

    /**
     * Carga el listado completo de libros y lo reenvia a la vista
     * de catalogo.
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("books", bookDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(request, response);
    }

    /**
     * Muestra el formulario de creacion de un libro nuevo, siempre
     * que el usuario tenga rol de bibliotecario.
     *
     * <p>
     * Si el usuario no esta autorizado, la peticion se redirige al
     * catalogo y el metodo retorna sin mostrar el formulario.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        request.setAttribute("book", new Book());
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/WEB-INF/views/book-form.jsp").forward(request, response);
    }

    /**
     * Muestra el formulario de edicion con los datos del libro cuyo
     * identificador se recibe como parametro.
     *
     * <p>
     * El metodo valida que el usuario sea bibliotecario, que el
     * parametro {@code id} sea un numero valido y que el libro
     * exista en la base de datos. Si cualquiera de estas condiciones
     * falla, se redirige al listado principal.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/books?action=list");
            return;
        }
        Optional<Book> bookOpt = bookDAO.findById(id);
        if (bookOpt.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/books?action=list");
            return;
        }
        request.setAttribute("book", bookOpt.get());
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/WEB-INF/views/book-form.jsp").forward(request, response);
    }

    /**
     * Procesa la creacion de un libro nuevo a partir de los datos
     * del formulario.
     *
     * <p>
     * Si la validacion falla, se vuelve a mostrar el formulario con
     * el mensaje de error correspondiente. Si todo es correcto, se
     * persiste el libro y se redirige al catalogo con un indicador
     * de exito.
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
        Book book = new Book();
        String error = loadAndValidate(request, book);
        if (error != null) {
            request.setAttribute("errorMessage", error);
            request.setAttribute("book", book);
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/WEB-INF/views/book-form.jsp").forward(request, response);
            return;
        }
        bookDAO.save(book);
        response.sendRedirect(request.getContextPath() + "/books?action=list&success=created");
    }

    /**
     * Procesa la actualizacion de un libro existente.
     *
     * <p>
     * Se obtiene el identificador del libro desde el parametro
     * {@code id}, se cargan y validan los datos del formulario y,
     * si todo es correcto, se aplican los cambios. En caso de error
     * de validacion o de identificador invalido, se vuelve a mostrar
     * el formulario con el mensaje correspondiente.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws ServletException si ocurre un error al reenviar a la JSP.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/books?action=list");
            return;
        }
        Book book = new Book();
        book.setId(id);
        String error = loadAndValidate(request, book);
        if (error != null) {
            request.setAttribute("errorMessage", error);
            request.setAttribute("book", book);
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/WEB-INF/views/book-form.jsp").forward(request, response);
            return;
        }
        bookDAO.update(book);
        response.sendRedirect(request.getContextPath() + "/books?action=list&success=updated");
    }

    /**
     * Elimina un libro a partir del identificador recibido como
     * parametro.
     *
     * <p>
     * Si el identificador es invalido, se omite la operacion y se
     * redirige igualmente al catalogo. Tras la operacion, se redirige
     * con un indicador de exito.
     * </p>
     *
     * @param request  peticion HTTP actual.
     * @param response respuesta HTTP actual.
     * @throws IOException si ocurre un error al enviar la redireccion.
     */
    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!isLibrarian(request, response)) {
            return;
        }
        Long id = parseLong(request.getParameter("id"));
        if (id != null) {
            bookDAO.delete(id);
        }
        response.sendRedirect(request.getContextPath() + "/books?action=list&success=deleted");
    }

    /**
     * Carga los parametros del formulario sobre el {@link Book}
     * entregado y valida que los datos obligatorios esten completos.
     *
     * <p>
     * El metodo realiza una validacion minima de presencia: titulo,
     * autor e ISBN no pueden estar vacios. La disponibilidad se
     * toma del parametro {@code available} (un checkbox) y se
     * interpreta como {@code true} si el parametro llega, lo que
     * refleja el comportamiento estandar de los formularios HTML.
     * </p>
     *
     * @param request peticion HTTP con los parametros del formulario.
     * @param book    instancia donde se cargan los valores leidos.
     * @return {@code null} si la validacion es correcta; en caso
     *         contrario, un mensaje de error listo para mostrar al
     *         usuario.
     */
    private String loadAndValidate(HttpServletRequest request, Book book) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String availableParam = request.getParameter("available");

        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setAvailable(availableParam != null); // checkbox: si llega, esta marcado.

        if (title == null || title.isBlank()) {
            return "El titulo del libro es obligatorio.";
        }
        if (author == null || author.isBlank()) {
            return "El autor del libro es obligatorio.";
        }
        if (isbn == null || isbn.isBlank()) {
            return "El ISBN del libro es obligatorio.";
        }
        return null;
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
     * para todas las acciones administrativas.
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
