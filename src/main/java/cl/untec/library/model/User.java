package cl.untec.library.model;

import java.util.Objects;

public class User {

  private final long id;
  private final String name;
  private final String email;
  private final Role role;

  public User(long id, String name, String email, Role role) {
    this.id = id;
    this.name = Objects.requireNonNull(name, "El nombre es obligatorio.");
    this.email = Objects.requireNonNull(email, "El correo es obligatorio.");
    this.role = Objects.requireNonNull(role, "El rol es obligatorio.");
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }

  public boolean isLibrarian() {
    return role == Role.LIBRARIAN;
  }

  public String getRoleLabel() {
    return role.getLabel();
  }
}
