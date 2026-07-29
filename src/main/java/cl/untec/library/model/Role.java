package cl.untec.library.model;

public enum Role {
    LIBRARIAN,
    STUDENT;

    public String getLabel() {
        return switch (this) {
            case LIBRARIAN -> "Bibliotecario";
            case STUDENT -> "Estudiante";
        };
    }
}
