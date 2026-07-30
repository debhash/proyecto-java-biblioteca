package cl.untec.library.dao;

import cl.untec.library.model.Role;
import cl.untec.library.model.User;
import cl.untec.library.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * DAO para conversar con la tabla de usuarios.
 * Me ayuda con el login, con la búsqueda por id y con el listado de estudiantes para los préstamos.
 */
public class UserDAO {

  private static final String SQL_FIND_BY_EMAIL_AND_PASSWORD =
    "SELECT id, name, email, role FROM app_user WHERE email = ? AND password = ?";
  private static final String SQL_FIND_BY_ID =
    "SELECT id, name, email, role FROM app_user WHERE id = ?";
  private static final String SQL_FIND_STUDENTS =
    "SELECT id, name, email, role FROM app_user WHERE role = ? ORDER BY name";

  /**
   * Busco un usuario por correo y contraseña.
   *
   * <p>
   * En esta versión dejo la contraseña en texto plano para no enredar el flujo.
   * TODO: más adelante conviene cambiar esto por hash + salt.
   * </p>
   *
   * @param email correo del usuario.
   * @param password contraseña en texto plano.
   * @return usuario encontrado o vacío si no coincide.
   */
  public Optional<User> findByEmailAndPassword(String email, String password) {
    return findByEmailAndPassword(
      email,
      password == null ? new char[0] : password.toCharArray()
    );
  }

  /**
   * Busco un usuario por correo y una contraseña que solo vive un momento en memoria.
   *
   * <p>
   * TODO: más adelante conviene pasar a hash + salt.
   * </p>
   *
   * @param email correo del usuario.
   * @param passwordChars contraseña temporal en memoria.
   * @return usuario encontrado o vacío si no coincide.
   */
  public Optional<User> findByEmailAndPassword(
    String email,
    char[] passwordChars
  ) {
    String password = new String(passwordChars);
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
    } finally {
      Arrays.fill(passwordChars, '\0');
    }
    return Optional.empty();
  }

  /**
   * Busco un usuario por su identificador.
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
   * Listo los usuarios con rol estudiante.
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
   * Convierto una fila de la tabla app_user en un objeto User.
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
