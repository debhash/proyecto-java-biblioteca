package cl.untec.biblioteca.dao;

import cl.untec.biblioteca.model.User;
import cl.untec.biblioteca.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO responsable de las consultas sobre la tabla app_user.
 * Todas las operaciones usan PreparedStatement y try-with-resources
 * para evitar inyeccion SQL.
 */
public class UserDAO {

    /** Consulta SQL para buscar un usuario por email y contrasena (autenticacion). */
    private static final String SQL_FIND_BY_EMAIL_AND_PASSWORD = "SELECT id, name, email, password, role FROM app_user WHERE email = ? AND password = ?";

    /** Consulta SQL para buscar un usuario por su identificador. */
    private static final String SQL_FIND_BY_ID = "SELECT id, name, email, password, role FROM app_user WHERE id = ?";

    /** Consulta SQL para listar todos los usuarios con rol STUDENT, ordenados por nombre. */
    private static final String SQL_FIND_STUDENTS = "SELECT id, name, email, password, role FROM app_user WHERE role = ? ORDER BY name";

    /**
     * Busca un usuario por email y contrasena. La comparacion de contrasena
     * se hace directamente en la consulta porque en este proyecto academico
     * se almacenan en texto plano; en produccion deberia compararse el hash.
     *
     * @param email    email a buscar
     * @param password contrasena en texto plano
     * @return Optional con el usuario encontrado, o vacio si las credenciales no coinciden
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_EMAIL_AND_PASSWORD)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar usuario por credenciales", ex);
        }
        return Optional.empty();
    }

    /**
     * Busca un usuario por su identificador unico.
     *
     * @param id identificador del usuario
     * @return Optional con el usuario encontrado, o vacio si no existe
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public Optional<User> findById(Long id) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar usuario por id", ex);
        }
        return Optional.empty();
    }

    /**
     * Lista los usuarios con rol STUDENT.
     * Se utiliza para que el bibliotecario seleccione a quien prestar.
     *
     * @return lista de estudiantes, posiblemente vacia, ordenada por nombre
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public List<User> findStudents() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_STUDENTS)) {
            statement.setString(1, User.ROLE_STUDENT);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    users.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar estudiantes", ex);
        }
        return users;
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto User.
     * Metodo auxiliar reutilizado por todas las consultas de esta clase.
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return instancia de User con los datos de la fila
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
