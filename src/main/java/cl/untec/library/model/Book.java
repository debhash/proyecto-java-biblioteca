package cl.untec.library.model;

/**
 * Esta clase representa un libro del catálogo.
 * Me sirve para listar, crear, editar y borrar registros de la biblioteca.
 */
public class Book {

  private long id;
  private String title;
  private String author;
  private String isbn;
  private boolean available;

  /**
   * Dejo este constructor vacío para poder armar objetos desde formularios y consultas SQL.
   */
  public Book() {}

  /**
   * Creo un libro con sus datos principales.
   *
   * @param id identificador interno.
   * @param title título del libro.
   * @param author autor del libro.
   * @param isbn código ISBN.
   * @param available estado de disponibilidad.
   */
  public Book(
    long id,
    String title,
    String author,
    String isbn,
    boolean available
  ) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.available = available;
  }

  /**
   * Obtengo el identificador del libro.
   *
   * @return id interno.
   */
  public long getId() {
    return id;
  }

  /**
   * Asigno el identificador del libro.
   *
   * @param id nuevo identificador.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Obtengo el título del libro.
   *
   * @return título.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Asigno el título del libro.
   *
   * @param title título.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Obtengo el autor del libro.
   *
   * @return autor.
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Asigno el autor del libro.
   *
   * @param author autor.
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Obtengo el ISBN del libro.
   *
   * @return ISBN.
   */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Asigno el ISBN del libro.
   *
   * @param isbn ISBN.
   */
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  /**
   * Pregunto si el libro está disponible.
   *
   * @return {@code true} si se puede prestar.
   */
  public boolean isAvailable() {
    return available;
  }

  /**
   * Marco si el libro está disponible o no.
   *
   * @param available nuevo estado.
   */
  public void setAvailable(boolean available) {
    this.available = available;
  }
}
