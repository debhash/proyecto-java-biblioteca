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
 * Listener para levantar la base de datos apenas parte la aplicación.
 * Primero dejo lista la estructura y, si la base está vacía, cargo los datos iniciales.
 */
@WebListener
public class DbInitializer implements ServletContextListener {

  /**
   * Se ejecuta cuando arranca el contexto de la aplicación.
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
   * No necesito hacer limpieza especial al cerrar la aplicación.
   *
   * @param sce evento de destrucción del contexto.
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

  /**
   * Ejecuto un script SQL que vive en el classpath.
   *
   * @param connection conexión JDBC activa.
   * @param resourceName nombre del script a ejecutar.
   * @throws Exception si el recurso no existe o si falla la ejecución.
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
   * Reviso si una tabla está vacía.
   *
   * @param connection conexión JDBC activa.
   * @param tableName nombre de la tabla.
   * @return {@code true} si no tiene filas.
   * @throws Exception si ocurre un problema con SQL.
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
