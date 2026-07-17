package cl.untec.biblioteca.dao;

import cl.untec.biblioteca.model.Book;
import cl.untec.biblioteca.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO responsable del CRUD de la tabla book.
 * Expone metodos pequenos y faciles de probar para el controlador.
 */
public class BookDAO {

    /** Consulta SQL para listar todos los libros ordenados por titulo. */
    private static final String SQL_FIND_ALL =
            "SELECT id, title, author, isbn, available FROM book ORDER BY title";

    /** Consulta SQL para buscar un libro por su identificador. */
    private static final String SQL_FIND_BY_ID =
            "SELECT id, title, author, isbn, available FROM book WHERE id = ?";

    /** Consulta SQL para insertar un nuevo libro en la tabla book. */
    private static final String SQL_INSERT =
            "INSERT INTO book (title, author, isbn, available) VALUES (?, ?, ?, ?)";

    /** Consulta SQL para actualizar los datos de un libro existente. */
    private static final String SQL_UPDATE =
            "UPDATE book SET title = ?, author = ?, isbn = ?, available = ? WHERE id = ?";

    /** Consulta SQL para eliminar un libro por su identificador. */
    private static final String SQL_DELETE =
            "DELETE FROM book WHERE id = ?";

    /** Consulta SQL para actualizar unicamente la disponibilidad de un libro (usada dentro de transacciones). */
    private static final String SQL_UPDATE_AVAILABILITY =
            "UPDATE book SET available = ? WHERE id = ?";

    /**
     * Lista todos los libros registrados en la biblioteca, ordenados por titulo.
     *
     * @return lista de libros, posiblemente vacia, ordenada alfabeticamente
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                books.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar libros", ex);
        }
        return books;
    }

    /**
     * Busca un libro por su identificador unico.
     *
     * @param id identificador del libro
     * @return Optional con el libro encontrado, o vacio si no existe
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public Optional<Book> findById(Long id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar libro por id", ex);
        }
        return Optional.empty();
    }

    /**
     * Persiste un nuevo libro en la base de datos. El identificador del
     * libro se ignora porque lo asigna la base de datos.
     *
     * @param book libro a crear (sin id)
     * @throws RuntimeException si ocurre un error de SQL al insertar
     */
    public void save(Book book) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setBoolean(4, book.isAvailable());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al crear libro", ex);
        }
    }

    /**
     * Actualiza los datos de un libro existente. Se identifica por su id.
     *
     * @param book libro con los datos nuevos (debe tener id)
     * @throws RuntimeException si ocurre un error de SQL al actualizar
     */
    public void update(Book book) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setBoolean(4, book.isAvailable());
            statement.setLong(5, book.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar libro", ex);
        }
    }

    /**
     * Elimina un libro por su identificador.
     *
     * @param id identificador del libro a eliminar
     * @throws RuntimeException si ocurre un error de SQL al eliminar
     */
    public void delete(Long id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar libro", ex);
        }
    }

    /**
     * Cambia la disponibilidad de un libro. Se usa desde LoanDAO
     * dentro de la misma transaccion para mantener la consistencia.
     *
     * @param connection conexion JDBC transaccional (no se cierra)
     * @param id         identificador del libro a actualizar
     * @param available  nuevo valor de disponibilidad
     * @throws SQLException si ocurre un error de SQL al actualizar
     */
    public void updateAvailability(Connection connection, Long id, boolean available) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_AVAILABILITY)) {
            statement.setBoolean(1, available);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Book.
     * Metodo auxiliar reutilizado por todas las consultas de esta clase.
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return instancia de Book con los datos de la fila
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private Book map(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setAvailable(rs.getBoolean("available"));
        return book;
    }
}
