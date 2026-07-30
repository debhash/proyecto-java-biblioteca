package cl.untec.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralizo acá la creación de conexiones JDBC para no repetir la configuración en varios lados.
 */
public final class DatabaseConnection {

  private static final String PROPERTIES_FILE = "database.properties";
  private static final Properties PROPERTIES = loadProperties();

  private DatabaseConnection() {}

  /**
   * Obtengo una conexión abierta contra la base configurada.
   *
   * @return conexión JDBC lista para usar.
   * @throws SQLException si no se puede abrir la conexión.
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
      PROPERTIES.getProperty("db.url"),
      PROPERTIES.getProperty("db.user"),
      PROPERTIES.getProperty("db.password")
    );
  }

  /**
   * Cargo las propiedades de base de datos desde el classpath.
   *
   * @return propiedades de conexión.
   */
  private static Properties loadProperties() {
    Properties properties = new Properties();
    try (
      InputStream inputStream = DatabaseConnection.class
        .getClassLoader()
        .getResourceAsStream(PROPERTIES_FILE)
    ) {
      if (inputStream == null) {
        throw new IllegalStateException("No se encontró database.properties.");
      }
      properties.load(inputStream);
      Class.forName(properties.getProperty("db.driver"));
      return properties;
    } catch (IOException | ClassNotFoundException ex) {
      throw new IllegalStateException(
        "No fue posible cargar la configuración de base de datos.",
        ex
      );
    }
  }
}
