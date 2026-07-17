package cl.untec.biblioteca.model;

/**
 * Representa un usuario del sistema.
 * El rol define los permisos que tiene dentro de la biblioteca.
 */
public class User {

    /** Codigo de rol para usuarios con permisos administrativos (alta y baja de libros, registro de prestamos y devoluciones). */
    public static final String ROLE_LIBRARIAN = "LIBRARIAN";

    /** Codigo de rol para usuarios finales que pueden consultar el catalogo y revisar sus propios prestamos. */
    public static final String ROLE_STUDENT = "STUDENT";

    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;

    /**
     * Constructor vacio requerido por frameworks y por la construccion
     * incremental del objeto mediante setters (por ejemplo al mapear un ResultSet).
     */
    public User() {
    }

    /**
     * Constructor completo para crear un usuario con todos sus campos ya conocidos.
     *
     * @param id       identificador unico del usuario (null si aun no fue persistido)
     * @param name     nombre completo del usuario
     * @param email    email de acceso, debe ser unico en la base de datos
     * @param password contrasena en texto plano (solo con fines academicos)
     * @param role     rol del usuario ({@link #ROLE_LIBRARIAN} o {@link #ROLE_STUDENT})
     */
    public User(Long id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Devuelve el identificador unico del usuario.
     *
     * @return id del usuario, o null si aun no fue persistido
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador unico del usuario. Usado por el DAO al persistir.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre completo del usuario.
     *
     * @return nombre completo
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre completo del usuario.
     *
     * @param name nombre completo
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Devuelve el email de acceso del usuario.
     *
     * @return email de acceso
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asigna el email de acceso del usuario.
     *
     * @param email email de acceso (debe ser unico)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve la contrasena del usuario en texto plano.
     * Solo se conserva asi con fines academicos; en produccion debe
     * almacenarse un hash seguro (por ejemplo BCrypt o Argon2).
     *
     * @return contrasena en texto plano
     */
    public String getPassword() {
        return password;
    }

    /**
     * Asigna la contrasena del usuario.
     *
     * @param password contrasena en texto plano
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Devuelve el codigo del rol del usuario.
     *
     * @return rol, uno de {@link #ROLE_LIBRARIAN} o {@link #ROLE_STUDENT}
     */
    public String getRole() {
        return role;
    }

    /**
     * Asigna el codigo del rol del usuario.
     *
     * @param role rol, uno de {@link #ROLE_LIBRARIAN} o {@link #ROLE_STUDENT}
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Indica si el usuario es bibliotecario/admin, lo que le permite
     * acceder a las paginas administrativas del sistema.
     *
     * @return true si el rol es {@link #ROLE_LIBRARIAN}, false en cualquier otro caso
     */
    public boolean isLibrarian() {
        return ROLE_LIBRARIAN.equals(role);
    }

    /**
     * Devuelve la etiqueta legible del rol en espanol para mostrarla en la interfaz.
     * Si el rol no coincide con ninguno conocido, se devuelve tal cual para
     * no perder informacion en pantalla.
     *
     * @return "BIBLIOTECARIO", "ESTUDIANTE" o el codigo del rol si no coincide
     */
    public String getRoleLabel() {
        if (ROLE_LIBRARIAN.equals(role)) {
            return "BIBLIOTECARIO";
        }
        if (ROLE_STUDENT.equals(role)) {
            return "ESTUDIANTE";
        }
        // Si el rol no coincide con ninguno conocido, se devuelve tal cual
        return role;
    }
}
