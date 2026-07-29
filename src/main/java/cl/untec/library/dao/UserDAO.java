package cl.untec.library.dao;

import cl.untec.library.model.Role;
import cl.untec.library.model.User;
import cl.untec.library.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO encargado de consultar usuarios en la base de datos.
 * Se usa para autenticación, búsqueda por id y selección de estudiantes en el flujo de préstamos.
 */
public class UserDAO {

  private static final String SQL_FIND_BY_EMAIL_AND_PASSWORD =
    "SELECT id, name, email, role FROM app_user WHERE email = ? AND password = ?";
  private static final String SQL_FIND_BY_ID =
    "SELECT id, name, email, role FROM app_user WHERE id = ?";
  private static final String SQL_FIND_STUDENTS =
    "SELECT id, name, email, role FROM app_user WHERE role = ? ORDER BY name";

  /**
   * Busca un usuario por correo y contraseña.
   *
   * @param email correo del usuario.
   * @param password contraseña en texto plano para este proyecto educativo.
   * @return usuario encontrado o vacío si no coincide.
   */
  public Optional<User> findByEmailAndPassword(String email, String password) {
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(
        SQL_FIND_BY_EMAIL_AND_PASSWORD
      )
    ) {
      statement.setString(1, email);
      statement.setString(2, password);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(map(resultSet));
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException(
        "Error al buscar usuario por credenciales.",
        ex
      );
    }
    return Optional.empty();
  }

  /**
   * Busca un usuario por su identificador.
   *
   * @param id identificador interno del usuario.
   * @return usuario encontrado o vacío si no existe.
   */
  public Optional<User> findById(long id) {
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
      throw new RuntimeException("Error al buscar usuario por id.", ex);
    }
    return Optional.empty();
  }

  /**
   * Lista los usuarios con rol estudiante.
   *
   * @return listado de estudiantes ordenado por nombre.
   */
  public List<User> findStudents() {
    List<User> users = new ArrayList<>();
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(
        SQL_FIND_STUDENTS
      )
    ) {
      statement.setString(1, Role.STUDENT.name());
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          users.add(map(resultSet));
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al listar estudiantes.", ex);
    }
    return users;
  }

  /**
   * Convierte una fila de la tabla app_user en un objeto User.
   *
   * @param resultSet resultado de la consulta.
   * @return usuario mapeado.
   * @throws SQLException si ocurre un error al leer columnas.
   */
  private User map(ResultSet resultSet) throws SQLException {
    return new User(
      resultSet.getLong("id"),
      resultSet.getString("name"),
      resultSet.getString("email"),
      Role.valueOf(resultSet.getString("role"))
    );
  }
}
