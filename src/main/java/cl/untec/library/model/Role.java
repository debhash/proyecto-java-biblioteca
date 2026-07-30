package cl.untec.library.model;

/**
 * Representa los roles disponibles en la aplicación de biblioteca digital.
 */
public enum Role {
  LIBRARIAN,
  STUDENT;

  /**
   * Devuelve el nombre visible del rol para mostrarlo en la interfaz.
   *
   * @return etiqueta del rol.
   */
  public String getLabel() {
    return switch (this) {
      case LIBRARIAN -> "Bibliotecario";
      case STUDENT -> "Estudiante";
    };
  }
}
