package cl.untec.library.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import org.h2.tools.RunScript;

/**
 * Inicializa la base de datos al arrancar la aplicación.
 * Primero crea la estructura con {@code schema.sql} y luego carga datos de prueba con {@code data.sql} si corresponde.
 */
@WebListener
public class DbInitializer implements ServletContextListener {

  /**
   * Se ejecuta al levantar el contexto de la aplicación.
   *
   * @param sce evento de inicialización del contexto.
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try (Connection connection = DatabaseConnection.getConnection()) {
      executeScript(connection, "schema.sql");
      if (
        isTableEmpty(connection, "APP_USER") && isTableEmpty(connection, "BOOK")
      ) {
        executeScript(connection, "data.sql");
      }
    } catch (Exception ex) {
      throw new RuntimeException(
        "No se pudo inicializar la base de datos.",
        ex
      );
    }
  }

  /**
   * Se ejecuta al detener el contexto de la aplicación.
   * En este proyecto no requiere limpieza especial.
   *
   * @param sce evento de destrucción del contexto.
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

  /**
   * Ejecuta un script SQL que vive en el classpath.
   *
   * @param connection conexión JDBC activa.
   * @param resourceName nombre del script a ejecutar.
   * @throws Exception si el recurso no existe o falla la ejecución.
   */
  private void executeScript(Connection connection, String resourceName)
    throws Exception {
    try (
      InputStream input = getClass()
        .getClassLoader()
        .getResourceAsStream(resourceName)
    ) {
      if (input == null) {
        throw new IllegalStateException(
          "No se encontró el recurso " + resourceName
        );
      }
      try (
        BufferedReader reader = new BufferedReader(
          new InputStreamReader(input, StandardCharsets.UTF_8)
        )
      ) {
        RunScript.execute(connection, reader);
      }
    }
  }

  /**
   * Indica si una tabla no tiene registros.
   *
   * @param connection conexión JDBC activa.
   * @param tableName nombre de la tabla a revisar.
   * @return {@code true} si la tabla está vacía.
   * @throws Exception si ocurre un error de SQL.
   */
  private boolean isTableEmpty(Connection connection, String tableName)
    throws Exception {
    try (
      Statement statement = connection.createStatement();
      var rs = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)
    ) {
      return rs.next() && rs.getInt(1) == 0;
    }
  }
}
