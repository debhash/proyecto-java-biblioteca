package cl.untec.library.model;

import java.time.LocalDate;

/**
 * Esta clase representa un préstamo de libro.
 * También guardo algunos datos auxiliares para mostrar información más clara en pantalla.
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
   * Dejo este constructor vacío para mapear filas de la base de datos con comodidad.
   */
  public Loan() {}

  /**
   * Creo un préstamo con sus campos principales.
   *
   * @param id identificador del préstamo.
   * @param userId id del usuario.
   * @param bookId id del libro.
   * @param loanDate fecha del préstamo.
   * @param returnDate fecha de devolución.
   * @param returned indica si ya fue devuelto.
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
   * Obtengo el identificador del préstamo.
   *
   * @return id del préstamo.
   */
  public Long getId() {
    return id;
  }

  /**
   * Asigno el identificador del préstamo.
   *
   * @param id identificador.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Obtengo el id del usuario asociado.
   *
   * @return id del usuario.
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * Asigno el id del usuario asociado.
   *
   * @param userId id del usuario.
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Obtengo el id del libro asociado.
   *
   * @return id del libro.
   */
  public Long getBookId() {
    return bookId;
  }

  /**
   * Asigno el id del libro asociado.
   *
   * @param bookId id del libro.
   */
  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  /**
   * Obtengo la fecha en que se hizo el préstamo.
   *
   * @return fecha del préstamo.
   */
  public LocalDate getLoanDate() {
    return loanDate;
  }

  /**
   * Asigno la fecha del préstamo.
   *
   * @param loanDate fecha del préstamo.
   */
  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }

  /**
   * Obtengo la fecha de devolución.
   *
   * @return fecha de devolución, o {@code null} si aún no se devuelve.
   */
  public LocalDate getReturnDate() {
    return returnDate;
  }

  /**
   * Asigno la fecha de devolución.
   *
   * @param returnDate fecha de devolución.
   */
  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  /**
   * Pregunto si el préstamo ya fue devuelto.
   *
   * @return {@code true} si está devuelto.
   */
  public boolean isReturned() {
    return returned;
  }

  /**
   * Marco el préstamo como devuelto o pendiente.
   *
   * @param returned nuevo estado.
   */
  public void setReturned(boolean returned) {
    this.returned = returned;
  }

  /**
   * Obtengo el nombre del usuario para mostrarlo en pantalla.
   *
   * @return nombre del usuario.
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Asigno el nombre del usuario.
   *
   * @param userName nombre visible.
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Obtengo el título del libro para mostrarlo en pantalla.
   *
   * @return título del libro.
   */
  public String getBookTitle() {
    return bookTitle;
  }

  /**
   * Asigno el título del libro.
   *
   * @param bookTitle título visible.
   */
  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }
}
