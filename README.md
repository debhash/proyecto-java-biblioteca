# Biblioteca Digital UNTEC

Armé esta aplicación web para administrar una biblioteca digital universitaria.
La construí con Jakarta EE, JSP, JSTL, JDBC, H2 y una estructura MVC + DAO para que todo quede más ordenado y fácil de seguir.

## Tecnologías que estoy usando

- Java 25 LTS
- Jakarta EE 10
- Jakarta Servlet 6.0
- Jakarta Server Pages (JSP) 3.1
- Jakarta Standard Tag Library (JSTL) 3.0
- JDBC
- H2 Database 2.2 (modo archivo)
- Apache Maven 3.9 o superior
- Apache Tomcat 10.1
- HTML + Bootstrap 5

## Lo que necesito antes de correrlo

- **JDK 25 LTS** instalado y configurado en `PATH` y `JAVA_HOME`.
- **Apache Maven 3.9 o superior**. Yo lo probé con 3.9.16.
- **Apache Tomcat 10.1.x** para desplegar la aplicación con `jakarta.*`.

## Cómo está organizado el proyecto

```text
src/main/java/cl/untec/library
├── controller/   # Servlets: LoginServlet, LogoutServlet, BookServlet, LoanServlet
├── dao/          # Acceso a datos con JDBC: UserDAO, BookDAO, LoanDAO
├── filter/       # Filtros: AuthenticationFilter
├── model/        # POJOs: User, Book, Loan
└── util/         # Utilidades: DatabaseConnection, DbInitializer
```

## Modelo de datos

### Tabla `app_user`

| Campo    | Tipo            | Descripción             |
| -------- | --------------- | ----------------------- |
| id       | BIGINT PK       | Identificador único     |
| name     | VARCHAR(100)    | Nombre completo         |
| email    | VARCHAR(120) UK | Correo de acceso        |
| password | VARCHAR(100)    | Contraseña              |
| role     | VARCHAR(20)     | `LIBRARIAN` o `STUDENT` |

### Tabla `book`

| Campo     | Tipo         | Descripción                |
| --------- | ------------ | -------------------------- |
| id        | BIGINT PK    | Identificador único        |
| title     | VARCHAR(200) | Título del libro           |
| author    | VARCHAR(150) | Autor                      |
| isbn      | VARCHAR(50)  | Código ISBN                |
| available | BOOLEAN      | `true` si se puede prestar |

### Tabla `loan`

| Campo       | Tipo      | Descripción                         |
| ----------- | --------- | ----------------------------------- |
| id          | BIGINT PK | Identificador único                 |
| user_id     | BIGINT FK | Usuario que solicita el préstamo    |
| book_id     | BIGINT FK | Libro prestado                      |
| loan_date   | DATE      | Fecha del préstamo                  |
| return_date | DATE NULL | Fecha en que se devolvió, si aplica |
| returned    | BOOLEAN   | `true` cuando ya se devolvió        |

## Credenciales de prueba

| Rol       | Email            | Contraseña      |
| --------- | ---------------- | --------------- |
| LIBRARIAN | `luis@untec.cl`  | `admin123`      |
| STUDENT   | `bruce@untec.cl` | `estudiante123` |

## Cómo generar el WAR

Desde la raíz del proyecto:

```bash
mvn clean package
```

El archivo generado queda en:

```text
target/digital-library.war
```

## Cómo desplegar con Docker

Dejé un `Dockerfile` multi-stage y un `docker-compose.yml` para correr la aplicación sin instalar Tomcat ni JDK en la máquina local.

### Requisitos para Docker

- **Docker Engine 24.0 o superior**.
- **Docker Compose v2**.

### Opción recomendada: Docker Compose

Desde la raíz del proyecto:

```bash
docker compose up -d --build
```

Con eso:

1. Compilo el WAR.
2. Levanto Tomcat 10.1 con JDK 25.
3. Dejo la base H2 persistiéndose en el volumen `library_data`.

Para ver los logs:

```bash
docker compose logs -f library
```

Para detener todo:

```bash
docker compose down
```

### Opción manual con Docker

```bash
# Construir la imagen
docker build -t digital-library:0.0.2 .

# Crear el volumen para la base
docker volume create library_data

# Ejecutar el contenedor
docker run -d \
    --name digital-library \
    -p 8080:8080 \
    -v library_data:/data \
    digital-library:0.0.2
```

Comandos útiles:

```bash
# Ver logs
docker logs -f digital-library

# Detener el contenedor
docker stop digital-library

# Eliminar el contenedor
docker rm digital-library

# Reiniciar el contenedor
docker restart digital-library
```

Si borro el volumen con `docker volume rm library_data`, se pierden los datos guardados en la base.

### Acceso a la aplicación

Cuando el contenedor está arriba, puedo acceder desde:

- `http://localhost:8080/digital-library`

Si quiero cambiar el puerto, edito la línea `ports` del `docker-compose.yml` o uso el flag `-p` al correr `docker run`.

### Personalización

Variables útiles que puedo definir en `docker-compose.yml` o con `-e` en `docker run`:

| Variable    | Default   | Descripción                                   |
| ----------- | --------- | --------------------------------------------- |
| `JAVA_OPTS` | _(vacío)_ | Flags de JVM, por ejemplo `-Xms256m -Xmx512m` |

Ejemplo:

```yaml
services:
  library:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - library_data:/data
    environment:
      - JAVA_OPTS=-Xms256m -Xmx512m
    container_name: digital-library
```

### Limpieza completa

```bash
docker compose down --rmi all -v
```

## Cómo desplegar en Apache Tomcat

### Opción A: copiar el WAR a `webapps`

1. Verifico que `JAVA_HOME` y `JRE_HOME` apunten al JDK 25.
2. Detengo Tomcat.
3. Copio `target/digital-library.war` a la carpeta `webapps`.
4. Inicio Tomcat.
5. Entro a:
   - `http://localhost:8080/digital-library/`

### Opción B: usar Tomcat Manager

1. Configuro un usuario con rol `manager-gui`.
2. Entro a `http://localhost:8080/manager/html`.
3. En `WAR file to deploy`, selecciono `digital-library.war`.
4. Hago clic en `Deploy`.
5. Uso el enlace que me muestra Tomcat.

## Cómo entiendo la arquitectura

- **Modelo**: mis clases POJO en `cl.untec.library.model`.
- **Vista**: mis JSP + JSTL en `src/main/webapp/WEB-INF/views/`.
- **Controlador**: mis Servlets en `cl.untec.library.controller`.
- **Acceso a datos**: mis DAO con JDBC en `cl.untec.library.dao`.

## Cómo estoy usando DAO

- `UserDAO`: login, búsqueda por id y listado de estudiantes.
- `BookDAO`: listar, buscar, crear, actualizar, eliminar y cambiar disponibilidad.
- `LoanDAO`: listar, listar por usuario, registrar préstamo y devolución con transacción JDBC.

## Transacciones JDBC

`LoanDAO.registerLoan` y `LoanDAO.registerReturn` trabajan con `commit` y `rollback` para que no queden datos a medias.

## Despliegue y acceso

La aplicación se despliega como archivo `WAR` en Apache Tomcat 10.1. Dejo el acceso público centrado en `/login` e `index.jsp`, y las rutas principales quedan protegidas por sesión en los servlets.
