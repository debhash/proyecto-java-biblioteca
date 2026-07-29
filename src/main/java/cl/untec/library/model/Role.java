package cl.untec.library.model;

/**
 * Representa los roles disponibles en la aplicación de biblioteca digital.
 * En este proyecto educativo solo se usan dos perfiles: bibliotecario y estudiante.
 */
public enum Role {
  LIBRARIAN,
  STUDENT;

  /**
   * Devuelve el nombre visible del rol para mostrarlo en la interfaz.
   *
   * @return etiqueta en español chileno del rol.
   */
  public String getLabel() {
    return switch (this) {
      case LIBRARIAN -> "Bibliotecario";
      case STUDENT -> "Estudiante";
    };
  }
}
