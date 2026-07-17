package cl.untec.biblioteca.model;

/**
 * Representa un libro disponible en el catalogo de la biblioteca.
 * El campo available permite saber si el libro puede ser prestado.
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Boolean available;

    /**
     * Constructor vacio requerido por frameworks y por la construccion
     * incremental del objeto al mapear un ResultSet.
     */
    public Book() {
    }

    /**
     * Constructor completo para crear un libro con todos sus campos ya conocidos.
     *
     * @param id        identificador unico del libro (null si aun no fue persistido)
     * @param title     titulo del libro
     * @param author    autor del libro
     * @param isbn      codigo ISBN del libro
     * @param available true si el libro esta disponible para prestamo, false en caso contrario
     */
    public Book(Long id, String title, String author, String isbn, Boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
    }

    /**
     * Devuelve el identificador unico del libro.
     *
     * @return id del libro, o null si aun no fue persistido
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador unico del libro. Usado por el DAO al persistir.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el titulo del libro.
     *
     * @return titulo del libro
     */
    public String getTitle() {
        return title;
    }

    /**
     * Asigna el titulo del libro.
     *
     * @param title titulo del libro
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Devuelve el autor del libro.
     *
     * @return autor del libro
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Asigna el autor del libro.
     *
     * @param author autor del libro
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Devuelve el codigo ISBN del libro.
     *
     * @return codigo ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Asigna el codigo ISBN del libro.
     *
     * @param isbn codigo ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Devuelve la disponibilidad del libro como Boolean (puede ser null
     * si la columna de la base no admitio NOT NULL).
     *
     * @return disponibilidad como Boolean, o null
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * Asigna la disponibilidad del libro.
     *
     * @param available true si esta disponible para prestamo
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * Helper que evita problemas con Boolean null en JSP EL,
     * ya que alli se trata como true/false segun corresponda.
     *
     * @return true si el libro esta disponible, false en cualquier otro caso
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(available);
    }
}
