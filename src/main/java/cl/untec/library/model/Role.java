package cl.untec.library.model;

/**
 * Enum para representar los roles que puede tener una persona dentro de la biblioteca digital.
 * En esta aplicación solo manejo dos perfiles: bibliotecario y estudiante.
 */
public enum Role {
  LIBRARIAN,
  STUDENT;

  /**
   * Convierto el rol técnico en una etiqueta más amable para mostrarla en pantalla.
   *
   * @return nombre visible del rol.
   */
  public String getLabel() {
    return switch (this) {
      case LIBRARIAN -> "Bibliotecario";
      case STUDENT -> "Estudiante";
    };
  }
}
