package cl.untec.library.model;

import java.util.Objects;

/**
 * Esta clase representa a una persona que entra al sistema.
 * Me sirve para autenticar, guardar la sesión y saber qué puede hacer cada usuario.
 */
public class User {

  private final long id;
  private final String name;
  private final String email;
  private final Role role;

  /**
   * Creo un usuario con los datos esenciales para moverlo por la aplicación.
   *
   * @param id identificador interno del usuario.
   * @param name nombre completo.
   * @param email correo de acceso.
   * @param role rol dentro del sistema.
   */
  public User(long id, String name, String email, Role role) {
    this.id = id;
    this.name = Objects.requireNonNull(name, "El nombre es obligatorio.");
    this.email = Objects.requireNonNull(email, "El correo es obligatorio.");
    this.role = Objects.requireNonNull(role, "El rol es obligatorio.");
  }

  /**
   * Obtengo el identificador interno.
   *
   * @return id del usuario.
   */
  public long getId() {
    return id;
  }

  /**
   * Obtengo el nombre completo.
   *
   * @return nombre visible.
   */
  public String getName() {
    return name;
  }

  /**
   * Obtengo el correo de acceso.
   *
   * @return correo del usuario.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Obtengo el rol asignado.
   *
   * @return rol interno.
   */
  public Role getRole() {
    return role;
  }

  /**
   * Pregunto si esta persona tiene perfil de bibliotecario.
   *
   * @return {@code true} si el rol es bibliotecario.
   */
  public boolean isLibrarian() {
    return role == Role.LIBRARIAN;
  }

  /**
   * Devuelvo el nombre amigable del rol para mostrarlo en la interfaz.
   *
   * @return etiqueta visible del rol.
   */
  public String getRoleLabel() {
    return role.getLabel();
  }
}
