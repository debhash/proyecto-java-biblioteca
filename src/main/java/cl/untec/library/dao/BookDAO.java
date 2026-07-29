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
