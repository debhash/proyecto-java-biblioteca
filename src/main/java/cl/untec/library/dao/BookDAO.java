package cl.untec.library.dao;

import cl.untec.library.model.Book;
import cl.untec.library.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO encargado de las operaciones sobre la tabla book.
 * Centraliza el acceso a datos para mantener la lógica SQL fuera de los servlets.
 */
public class BookDAO {

  private static final String SQL_FIND_ALL =
    "SELECT id, title, author, isbn, available FROM book ORDER BY title";
  private static final String SQL_FIND_AVAILABLE =
    "SELECT id, title, author, isbn, available FROM book WHERE available = TRUE ORDER BY title";
  private static final String SQL_FIND_BY_ID =
    "SELECT id, title, author, isbn, available FROM book WHERE id = ?";
  private static final String SQL_CREATE =
    "INSERT INTO book (title, author, isbn, available) VALUES (?, ?, ?, ?)";
  private static final String SQL_UPDATE =
    "UPDATE book SET title = ?, author = ?, isbn = ?, available = ? WHERE id = ?";
  private static final String SQL_DELETE = "DELETE FROM book WHERE id = ?";
  private static final String SQL_UPDATE_AVAILABILITY =
    "UPDATE book SET available = ? WHERE id = ?";

  /**
   * Lista todos los libros del catálogo.
   *
   * @return listado completo de libros.
   */
  public List<Book> findAll() {
    List<Book> books = new ArrayList<>();
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        books.add(map(resultSet));
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al listar libros.", ex);
    }
    return books;
  }

  /**
   * Lista solo los libros disponibles para préstamo.
   *
   * @return listado de libros disponibles.
   */
  public List<Book> findAvailable() {
    List<Book> books = new ArrayList<>();
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(
        SQL_FIND_AVAILABLE
      );
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        books.add(map(resultSet));
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al listar libros disponibles.", ex);
    }
    return books;
  }

  /**
   * Busca un libro por su identificador.
   *
   * @param id identificador del libro.
   * @return libro encontrado o vacío.
   */
  public Optional<Book> findById(long id) {
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)
    ) {
      statement.setLong(1, id);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(map(resultSet));
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al buscar libro por id.", ex);
    }
    return Optional.empty();
  }

  /**
   * Crea un libro nuevo y devuelve el id generado.
   *
   * @param book libro a insertar.
   * @return id generado por la base de datos.
   */
  public long create(Book book) {
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(
        SQL_CREATE,
        PreparedStatement.RETURN_GENERATED_KEYS
      )
    ) {
      statement.setString(1, book.getTitle());
      statement.setString(2, book.getAuthor());
      statement.setString(3, book.getIsbn());
      statement.setBoolean(4, book.isAvailable());
      statement.executeUpdate();

      try (ResultSet keys = statement.getGeneratedKeys()) {
        if (keys.next()) {
          return keys.getLong(1);
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al crear libro.", ex);
    }
    return 0L;
  }

  /**
   * Actualiza un libro existente.
   *
   * @param book libro con los datos modificados.
   * @return {@code true} si se actualizó al menos un registro.
   */
  public boolean update(Book book) {
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)
    ) {
      statement.setString(1, book.getTitle());
      statement.setString(2, book.getAuthor());
      statement.setString(3, book.getIsbn());
      statement.setBoolean(4, book.isAvailable());
      statement.setLong(5, book.getId());
      return statement.executeUpdate() > 0;
    } catch (SQLException ex) {
      throw new RuntimeException("Error al actualizar libro.", ex);
    }
  }

  /**
   * Elimina un libro por su id.
   *
   * @param id identificador del libro.
   * @return {@code true} si se eliminó el registro.
   */
  public boolean delete(long id) {
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(SQL_DELETE)
    ) {
      statement.setLong(1, id);
      return statement.executeUpdate() > 0;
    } catch (SQLException ex) {
      throw new RuntimeException("Error al eliminar libro.", ex);
    }
  }

  /**
   * Cambia la disponibilidad del libro dentro de una transacción externa.
   *
   * @param connection conexión JDBC activa.
   * @param bookId id del libro.
   * @param available nuevo estado.
   * @throws SQLException si falla la actualización.
   */
  public void updateAvailability(
    Connection connection,
    Long bookId,
    boolean available
  ) throws SQLException {
    try (
      PreparedStatement statement = connection.prepareStatement(
        SQL_UPDATE_AVAILABILITY
      )
    ) {
      statement.setBoolean(1, available);
      statement.setLong(2, bookId);
      statement.executeUpdate();
    }
  }

  /**
   * Convierte una fila de la tabla book en un objeto Book.
   *
   * @param resultSet resultado de la consulta.
   * @return libro mapeado.
   * @throws SQLException si ocurre un error al leer columnas.
   */
  private Book map(ResultSet resultSet) throws SQLException {
    return new Book(
      resultSet.getLong("id"),
      resultSet.getString("title"),
      resultSet.getString("author"),
      resultSet.getString("isbn"),
      resultSet.getBoolean("available")
    );
  }
}
