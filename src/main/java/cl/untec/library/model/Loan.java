package cl.untec.library.model;

import java.time.LocalDate;

/**
 * Representa un préstamo de libro dentro de la biblioteca digital.
 * Incluye datos de negocio y algunos campos auxiliares para mostrar información legible en las vistas.
 */
public class Loan {

  private Long id;
  private Long userId;
  private Long bookId;
  private LocalDate loanDate;
  private LocalDate returnDate;
  private boolean returned;
  private String userName;
  private String bookTitle;

  /**
   * Constructor vacío necesario para mapear filas de la base de datos de forma simple.
   */
  public Loan() {}

  /**
   * Crea un préstamo con sus campos principales.
   *
   * @param id identificador del préstamo.
   * @param userId id del usuario que hizo el préstamo.
   * @param bookId id del libro prestado.
   * @param loanDate fecha en que se realizó el préstamo.
   * @param returnDate fecha de devolución, si ya existe.
   * @param returned indica si el préstamo ya fue devuelto.
   */
  public Loan(
    Long id,
    Long userId,
    Long bookId,
    LocalDate loanDate,
    LocalDate returnDate,
    boolean returned
  ) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.loanDate = loanDate;
    this.returnDate = returnDate;
    this.returned = returned;
  }

  /**
   * Obtiene el identificador del préstamo.
   *
   * @return id del préstamo.
   */
  public Long getId() {
    return id;
  }

  /**
   * Asigna el identificador del préstamo.
   *
   * @param id identificador.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Obtiene el id del usuario asociado al préstamo.
   *
   * @return id del usuario.
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * Asigna el id del usuario asociado al préstamo.
   *
   * @param userId id del usuario.
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Obtiene el id del libro asociado al préstamo.
   *
   * @return id del libro.
   */
  public Long getBookId() {
    return bookId;
  }

  /**
   * Asigna el id del libro asociado al préstamo.
   *
   * @param bookId id del libro.
   */
  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  /**
   * Obtiene la fecha en que se hizo el préstamo.
   *
   * @return fecha de préstamo.
   */
  public LocalDate getLoanDate() {
    return loanDate;
  }

  /**
   * Asigna la fecha de préstamo.
   *
   * @param loanDate fecha del préstamo.
   */
  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }

  /**
   * Obtiene la fecha de devolución.
   *
   * @return fecha de devolución, o {@code null} si aún no se devuelve.
   */
  public LocalDate getReturnDate() {
    return returnDate;
  }

  /**
   * Asigna la fecha de devolución.
   *
   * @param returnDate fecha de devolución.
   */
  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  /**
   * Indica si el préstamo fue devuelto.
   *
   * @return {@code true} si ya fue devuelto.
   */
  public boolean isReturned() {
    return returned;
  }

  /**
   * Define si el préstamo fue devuelto.
   *
   * @param returned nuevo estado.
   */
  public void setReturned(boolean returned) {
    this.returned = returned;
  }

  /**
   * Obtiene el nombre del usuario, usado solo para mostrar en pantalla.
   *
   * @return nombre del usuario.
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Asigna el nombre del usuario para mostrarlo en las vistas.
   *
   * @param userName nombre del usuario.
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Obtiene el título del libro, usado solo para mostrar en pantalla.
   *
   * @return título del libro.
   */
  public String getBookTitle() {
    return bookTitle;
  }

  /**
   * Asigna el título del libro para mostrarlo en las vistas.
   *
   * @param bookTitle título del libro.
   */
  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }
}
