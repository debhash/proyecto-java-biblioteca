package cl.untec.biblioteca.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centraliza la configuracion y obtencion de conexiones JDBC hacia H2.
 * Se carga desde el archivo database.properties del classpath
 * si cambiamos el motor solo necesitamos modificar aqui
 */
public class DatabaseConnection {

    /** Nombre del archivo de propiedades que contiene la configuracion JDBC. */
    private static final String PROPERTIES_FILE = "database.properties";

    /** URL JDBC por defecto hacia H2 en modo archivo, dentro de la carpeta library del directorio de trabajo. */
    private static final String DEFAULT_URL = "jdbc:h2:./library/library-db;AUTO_SERVER=TRUE";

    /** Usuario JDBC por defecto (H2 trae "sa" como superusuario inicial). */
    private static final String DEFAULT_USER = "sa";

    /** Contrasena JDBC por defecto (vacia, valida para el usuario sa de H2 en este proyecto academico). */
    private static final String DEFAULT_PASSWORD = "";

    /** Clase del driver JDBC por defecto. */
    private static final String DEFAULT_DRIVER = "org.h2.Driver";

    /** URL JDBC efectiva, cargada desde el properties o usando el valor por defecto. */
    private static String url;

    /** Usuario JDBC efectivo. */
    private static String user;

    /** Contrasena JDBC efectiva. */
    private static String password;

    /** Clase del driver JDBC efectivo. */
    private static String driver;

    // Se inicializa una sola vez al cargar la clase.
    static {
        loadProperties();
    }

    /**
     * Constructor privado para evitar que la clase utilitaria sea instanciada.
     */
    private DatabaseConnection() {
        // Clase utilitaria: no se debe instanciar.
    }

    /**
     * Carga la configuracion JDBC desde el archivo database.properties del
     * classpath. Si el archivo no existe o no puede leerse, se conservan
     * los valores por defecto. Si el driver no se encuentra, se lanza
     * ExceptionInInitializerError para impedir el arranque de la aplicacion.
     *
     * @throws ExceptionInInitializerError si la clase del driver JDBC no esta disponible
     */
    private static void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            // Si no se puede leer el archivo, se usan los valores por defecto.
            System.err.println("No se pudo leer " + PROPERTIES_FILE + ", se usaran valores por defecto.");
        }
        url = properties.getProperty("db.url", DEFAULT_URL);
        user = properties.getProperty("db.user", DEFAULT_USER);
        password = properties.getProperty("db.password", DEFAULT_PASSWORD);
        driver = properties.getProperty("db.driver", DEFAULT_DRIVER);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("Driver JDBC no encontrado: " + driver);
        }
    }

    /**
     * Obtiene una conexion nueva. Cada DAO se encarga de cerrarla
     * mediante try-with-resources para liberar los recursos.
     *
     * @return una nueva conexion JDBC a la base configurada
     * @throws SQLException si el driver falla o las credenciales son invalidas
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Devuelve la URL JDBC efectiva, util para logueo o diagnostico.
     *
     * @return URL JDBC configurada (por properties o por defecto)
     */
    public static String getUrl() {
        return url;
    }
}
