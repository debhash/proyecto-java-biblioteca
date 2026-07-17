package cl.untec.biblioteca.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.h2.tools.RunScript;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Inicializa la base de datos al arrancar la aplicacion.
 * 1. Asegura que las tablas existan ejecutando schema.sql.
 * 2. Inserta los datos de prueba solo si las tablas estan vacias.
 * De este modo el sistema puede reiniciarse sin perder informacion.
 */
@WebListener
public class DbInitializer implements ServletContextListener {

    /**
     * Se ejecuta automaticamente al levantar el contexto de la aplicacion.
     * Crea las tablas si no existen y carga los datos de prueba unicamente
     * cuando la base esta vacia, para no sobrescribir informacion existente.
     *
     * @param sce evento de inicializacion del contexto de servlet
     * @throws RuntimeException si la inicializacion falla (propaga el error para impedir el arranque)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[DbInitializer] Inicializando base de datos H2...");
        try (Connection connection = DatabaseConnection.getConnection()) {

            // 1) Crear la estructura de tablas.
            executeScript(connection, "schema.sql");
            System.out.println("[DbInitializer] Esquema verificado correctamente.");

            // 2) Insertar datos iniciales solo si la base esta vacia.
            if (isTableEmpty(connection, "APP_USER") && isTableEmpty(connection, "BOOK")) {
                executeScript(connection, "data.sql");
                System.out.println("[DbInitializer] Datos de prueba cargados.");
            } else {
                System.out.println("[DbInitializer] La base ya contiene datos, no se reinicio.");
            }

        } catch (Exception ex) {
            // Se registra el error y se deja que la aplicacion falle de forma controlada.
            System.err.println("[DbInitializer] Error al inicializar la base de datos: " + ex.getMessage());
            throw new RuntimeException("No se pudo inicializar la base de datos", ex);
        }
    }

    /**
     * Se ejecuta al detener el contexto de la aplicacion. En este proyecto
     * no se requiere limpieza porque la base de datos H2 persiste en disco
     * y no maneja pool de conexiones propio.
     *
     * @param sce evento de destruccion del contexto de servlet
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    /**
     * Carga un script SQL desde el classpath y lo ejecuta contra la conexion indicada.
     * Se usa para crear las tablas (schema.sql) y cargar los datos de prueba (data.sql).
     *
     * @param connection   conexion JDBC sobre la que se ejecuta el script
     * @param resourceName nombre del archivo SQL dentro del classpath
     * @throws Exception si el recurso no existe o la ejecucion del script falla
     */
    private void executeScript(Connection connection, String resourceName) throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalStateException("No se encontro el recurso " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                RunScript.execute(connection, reader);
            }
        }
    }

    /**
     * Indica si una tabla existe y no contiene filas. Se utiliza para
     * decidir si corresponde cargar los datos de prueba iniciales.
     *
     * @param connection conexion JDBC activa
     * @param tableName  nombre de la tabla a inspeccionar
     * @return true si la tabla tiene cero filas, false si tiene al menos una
     * @throws Exception si ocurre un error de SQL al ejecutar el conteo
     */
    private boolean isTableEmpty(Connection connection, String tableName) throws Exception {
        try (Statement statement = connection.createStatement();
                var rs = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}
