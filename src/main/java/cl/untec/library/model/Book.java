package cl.untec.library.model;

/**
 * Representa un libro del catálogo de la biblioteca digital.
 * Se usa tanto para listar libros como para crear, editar y eliminar registros.
 */
public class Book {

  private long id;
  private String title;
  private String author;
  private String isbn;
  private boolean available;

  /**
   * Constructor vacío requerido para completar formularios y mapear datos de forma simple.
   */
  public Book() {}

  /**
   * Crea un libro con todos sus datos principales.
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
   * Obtiene el identificador del libro.
   *
   * @return id interno.
   */
  public long getId() {
    return id;
  }

  /**
   * Asigna el identificador del libro.
   *
   * @param id nuevo identificador.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Obtiene el título del libro.
   *
   * @return título.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Asigna el título del libro.
   *
   * @param title título.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Obtiene el autor del libro.
   *
   * @return autor.
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Asigna el autor del libro.
   *
   * @param author autor.
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Obtiene el ISBN del libro.
   *
   * @return ISBN.
   */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Asigna el ISBN del libro.
   *
   * @param isbn ISBN.
   */
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  /**
   * Indica si el libro está disponible.
   *
   * @return {@code true} si se puede prestar.
   */
  public boolean isAvailable() {
    return available;
  }

  /**
   * Define si el libro está disponible.
   *
   * @param available nuevo estado.
   */
  public void setAvailable(boolean available) {
    this.available = available;
  }
}
