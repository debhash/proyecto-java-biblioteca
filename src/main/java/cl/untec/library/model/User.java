package cl.untec.library.model;

import java.util.Objects;

/**
 * Representa a un usuario de la biblioteca digital.
 * En esta versión educativa el usuario se usa para autenticación, sesión y autorización básica.
 */
public class User {

  private final long id;
  private final String name;
  private final String email;
  private final Role role;

  /**
   * Crea una instancia de usuario con los datos esenciales.
   *
   * @param id identificador interno del usuario.
   * @param name nombre completo del usuario.
   * @param email correo de acceso.
   * @param role rol asignado dentro de la aplicación.
   */
  public User(long id, String name, String email, Role role) {
    this.id = id;
    this.name = Objects.requireNonNull(name, "El nombre es obligatorio.");
    this.email = Objects.requireNonNull(email, "El correo es obligatorio.");
    this.role = Objects.requireNonNull(role, "El rol es obligatorio.");
  }

  /**
   * Obtiene el identificador del usuario.
   *
   * @return id interno.
   */
  public long getId() {
    return id;
  }

  /**
   * Obtiene el nombre completo del usuario.
   *
   * @return nombre visible.
   */
  public String getName() {
    return name;
  }

  /**
   * Obtiene el correo del usuario.
   *
   * @return correo de acceso.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Obtiene el rol del usuario.
   *
   * @return rol interno.
   */
  public Role getRole() {
    return role;
  }

  /**
   * Indica si el usuario tiene perfil de bibliotecario.
   *
   * @return {@code true} si es bibliotecario; {@code false} en caso contrario.
   */
  public boolean isLibrarian() {
    return role == Role.LIBRARIAN;
  }

  /**
   * Devuelve la etiqueta visible del rol para la interfaz.
   *
   * @return nombre amigable del rol.
   */
  public String getRoleLabel() {
    return role.getLabel();
  }
}
