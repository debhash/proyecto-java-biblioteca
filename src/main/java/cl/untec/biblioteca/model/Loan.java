package cl.untec.biblioteca.model;

import java.time.LocalDate;

/**
 * Representa el prestamo de un libro por parte de un usuario.
 * Los campos userName y bookTitle se utilizan para mostrar
 * informacion legible en las vistas, evitando joins en la JSP.
 */
public class Loan {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Boolean returned;

    // Campos auxiliares opcionales que se completan al hacer JOIN en el DAO.
    private String userName;
    private String bookTitle;

    /**
     * Constructor vacio requerido por frameworks y por la construccion
     * incremental del objeto al mapear un ResultSet.
     */
    public Loan() {
    }

    /**
     * Constructor basico con los campos de la tabla loan.
     * Los campos auxiliares (userName, bookTitle) quedan en null y
     * deben completarse por separado si la vista los requiere.
     *
     * @param id         identificador unico del prestamo (null si aun no fue persistido)
     * @param userId     id del usuario que solicita el prestamo
     * @param bookId     id del libro prestado
     * @param loanDate   fecha en que se realizo el prestamo
     * @param returnDate fecha de devolucion (null si aun no se devolvio)
     * @param returned   true si el prestamo ya fue devuelto, false en caso contrario
     */
    public Loan(Long id, Long userId, Long bookId,
                LocalDate loanDate, LocalDate returnDate, Boolean returned) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }

    /**
     * Devuelve el identificador unico del prestamo.
     *
     * @return id del prestamo, o null si aun no fue persistido
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador unico del prestamo.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el id del usuario que realizo el prestamo.
     *
     * @return id del usuario
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Asigna el id del usuario que realizo el prestamo.
     *
     * @param userId id del usuario
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Devuelve el id del libro prestado.
     *
     * @return id del libro
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * Asigna el id del libro prestado.
     *
     * @param bookId id del libro
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * Devuelve la fecha en que se realizo el prestamo.
     *
     * @return fecha del prestamo
     */
    public LocalDate getLoanDate() {
        return loanDate;
    }

    /**
     * Asigna la fecha en que se realizo el prestamo.
     *
     * @param loanDate fecha del prestamo
     */
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Devuelve la fecha en que se devolvio el libro.
     *
     * @return fecha de devolucion, o null si aun no se devolvio
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Asigna la fecha de devolucion del libro.
     *
     * @param returnDate fecha de devolucion (null si aun no se devolvio)
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Devuelve el flag de devolucion como Boolean.
     *
     * @return true si el prestamo esta marcado como devuelto, false en caso contrario
     */
    public Boolean getReturned() {
        return returned;
    }

    /**
     * Asigna el flag de devolucion.
     *
     * @param returned true si el prestamo fue devuelto
     */
    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

    /**
     * Devuelve el nombre del usuario que realizo el prestamo.
     * Este campo se completa al hacer JOIN en el DAO y no forma
     * parte de la tabla loan.
     *
     * @return nombre del usuario, o null si no se completo via JOIN
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Asigna el nombre del usuario para mostrarlo en la vista.
     *
     * @param userName nombre del usuario
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Devuelve el titulo del libro prestado.
     * Este campo se completa al hacer JOIN en el DAO y no forma
     * parte de la tabla loan.
     *
     * @return titulo del libro, o null si no se completo via JOIN
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Asigna el titulo del libro para mostrarlo en la vista.
     *
     * @param bookTitle titulo del libro
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Helper que evita problemas con Boolean null en JSP EL,
     * tratandolo como true/false segun corresponda.
     *
     * @return true si el prestamo ya fue devuelto, false en cualquier otro caso
     */
    public boolean isReturned() {
        return Boolean.TRUE.equals(returned);
    }
}
