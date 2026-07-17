# Biblioteca Digital UNTEC

Aplicacion web academica para administrar una biblioteca digital universitaria.
Desarrollada con Jakarta EE 10 (Servlets + JSP + JSTL), JDBC, H2 y patron MVC + DAO.

## Tecnologias utilizadas

- Java 25 LTS
- Jakarta EE 10
- Jakarta Servlet 6.0
- Jakarta Server Pages (JSP) 3.1
- Jakarta Standard Tag Library (JSTL) 3.0
- JDBC
- H2 Database 2.2 (modo archivo)
- Apache Maven 3.9 o superior (requerido para compilar con Java 25)
- Apache Tomcat 10.1
- HTML + Bootstrap 5

## Requisitos previos

- **JDK 25 LTS** (Temurin, Oracle, Zulu u otro) instalado y configurado en `PATH` y `JAVA_HOME`.
- Apache Maven 3.9 o superior (3.9.16 es la version minima verificada con este proyecto).
- Apache Tomcat 10.1.x (soporta el namespace `jakarta.*` y se ejecuta correctamente sobre JDK 25).
  s
## Estructura de paquetes

```
src/main/java/cl/untec/biblioteca
├── controller/   # Servlets (LoginServlet, LogoutServlet, BookServlet, LoanServlet)
├── dao/          # Acceso a datos con JDBC (UserDAO, BookDAO, LoanDAO)
├── filter/       # Filtros (AuthenticationFilter)
├── model/        # POJOs (User, Book, Loan)
└── util/         # Utilidades (DatabaseConnection, DbInitializer)
```

## Modelo de datos

### Tabla `app_user`

| Campo    | Tipo            | Descripcion                 |
|----------|-----------------|-----------------------------|
| id       | BIGINT PK       | Identificador unico         |
| name     | VARCHAR(100)    | Nombre completo             |
| email    | VARCHAR(120) UK | Email de acceso             |
| password | VARCHAR(100)    | Contrasena (solo academico) |
| role     | VARCHAR(20)     | LIBRARIAN o STUDENT         |

### Tabla `book`

| Campo     | Tipo            | Descripcion                   |
|-----------|-----------------|-------------------------------|
| id        | BIGINT PK       | Identificador unico           |
| title     | VARCHAR(200)    | Titulo del libro              |
| author    | VARCHAR(150)    | Autor                         |
| isbn      | VARCHAR(50)     | Codigo ISBN                   |
| available | BOOLEAN         | true si esta en la biblioteca |

### Tabla `loan`

| Campo       | Tipo        | Descripcion                          |
|-------------|-------------|--------------------------------------|
| id          | BIGINT PK   | Identificador unico                  |
| user_id     | BIGINT FK   | Usuario que solicita el prestamo     |
| book_id     | BIGINT FK   | Libro prestado                       |
| loan_date   | DATE        | Fecha del prestamo                   |
| return_date | DATE NULL   | Fecha en que se devolvio (si aplica) |
| returned    | BOOLEAN     | true cuando se devolvio              |

## Credenciales de prueba

| Rol       | Email                    | Contrasena      |
|-----------|--------------------------|-----------------|
| LIBRARIAN | `luis@untec.cl`          | `admin123`      |
| STUDENT   | `bruce@untec.cl`         | `estudiante123` |

## Generar el archivo WAR

Desde la raiz del proyecto:

```bash
mvn clean package
```

El archivo generado queda en:

```
target/biblioteca-digital.war
```

## Desplegar con Docker

El proyecto incluye un `Dockerfile` multi-stage y un `docker-compose.yml`
para construir y ejecutar la aplicacion en un contenedor sin necesidad de
tener instalado JDK 25 ni Tomcat en el equipo host.

### Requisitos previos

- **Docker Engine 24.0 o superior**.
- **Docker Compose v2** (incluido como `docker compose` en Docker Desktop
  y en la mayoria de distribuciones Linux modernas).

> El primer build descarga aproximadamente 1 GB entre la imagen base de
> Maven, el JDK 25 y Tomcat 10.1. Las builds posteriores reutilizan la
> cache de capas y demoran significativamente menos.

### Construir y ejecutar con Docker Compose (recomendado)

Desde la raiz del proyecto:

```bash
docker compose up -d --build
```

Este comando:

1. Construye la imagen segun el `Dockerfile` (compila el WAR y lo despliega
   en Tomcat 10.1 sobre JDK 25).
2. Levanta el servicio `biblioteca` en segundo plano.
3. Crea el volumen `biblioteca_data` para persistir la base H2.

Para ver los logs en tiempo real:

```bash
docker compose logs -f biblioteca
```

Para detener el servicio:

```bash
docker compose down
```

### Construir y ejecutar con Docker (forma manual)

Si prefiere usar los comandos `docker` directamente:

```bash
# Construir la imagen
docker build -t biblioteca-digital:1.0 .

# Crear un volumen para persistir la base de datos
docker volume create biblioteca_data

# Ejecutar el contenedor
docker run -d \
    --name biblioteca-digital \
    -p 8080:8080 \
    -v biblioteca_data:/data \
    biblioteca-digital:1.0
```

Comandos utiles:

```bash
# Ver logs
docker logs -f biblioteca-digital

# Detener el contenedor
docker stop biblioteca-digital

# Eliminar el contenedor (conservando el volumen con los datos)
docker rm biblioteca-digital

# Reiniciar el contenedor
docker restart biblioteca-digital
```

> Si elimina el volumen con `docker volume rm biblioteca_data`, los datos
> de la biblioteca (usuarios, libros y prestamos) se perderan de forma
> permanente.

### Acceso a la aplicacion

Una vez que el contenedor este corriendo, la aplicacion queda disponible en:

- `http://localhost:8080/`

El puerto `8080` del contenedor se mapea al `8080` del host. Para cambiarlo
edite la linea `ports` del `docker-compose.yml` o el flag `-p` del comando
`docker run`.

### Personalizacion

Variables de entorno utiles que pueden definirse en el bloque
`environment` del `docker-compose.yml` o con `-e` en `docker run`:

| Variable      | Default              | Descripcion                                |
|---------------|----------------------|--------------------------------------------|
| `JAVA_OPTS`   | *(vacio)*            | Flags de JVM, por ejemplo `-Xms256m -Xmx512m` |

Ejemplo aplicado al `docker-compose.yml`:

```yaml
services:
  biblioteca:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - biblioteca_data:/data
    environment:
      - JAVA_OPTS=-Xms256m -Xmx512m
    container_name: biblioteca-digital
```

### Limpieza completa

Para eliminar la imagen, el contenedor y el volumen (deja el sistema como
si nunca se hubiera desplegado):

```bash
docker compose down --rmi all -v
```

## Desplegar en Apache Tomcat

### Opcion A: copiar el WAR a la carpeta `webapps`

1. Asegurarse de que `JAVA_HOME` y `JRE_HOME` de Tomcat apunten al JDK 25.
2. Detener Tomcat (`bin/shutdown.sh` o desde el `Services` panel en Windows).
3. Copiar `target/biblioteca-digital.war` a la carpeta `webapps` de Tomcat.
4. Iniciar Tomcat (`bin/startup.sh` o `bin/startup.bat`).
5. Acceder desde el navegador a:
   - `http://localhost:8080/biblioteca-digital/`

### Opcion B: mediante Tomcat Manager

1. Configurar un usuario con rol `manager-gui` en `conf/tomcat-users.xml`.
2. Iniciar Tomcat y entrar a `http://localhost:8080/manager/html`.
3. En la seccion `WAR file to deploy`, seleccionar `biblioteca-digital.war`.
4. Presionar `Deploy`.
5. Acceder a la aplicacion desde el enlace mostrado en la lista.

## Patron MVC

- **Modelo**: clases POJO en `cl.untec.biblioteca.model`.
- **Vista**: JSP + JSTL en `src/main/webapp/WEB-INF/views/`. No contienen codigo Java ni JDBC.
- **Controlador**: Servlets en `cl.untec.biblioteca.controller` reciben la peticion HTTP, llaman a los DAO y reenvian a la vista correspondiente con `RequestDispatcher`.
- **Acceso a datos**: DAOs en `cl.untec.biblioteca.dao` que usan JDBC a traves de `DatabaseConnection`.

## Patron DAO

Cada tabla tiene su propio DAO que centraliza todas las consultas SQL:
- `UserDAO`: buscar por credenciales, por id, listar estudiantes.
- `BookDAO`: listar, buscar, crear, actualizar, eliminar y cambiar disponibilidad.
- `LoanDAO`: listar, listar por usuario, registrar prestamo y devolucion (con transaccion JDBC).

## Transacciones JDBC

`LoanDAO.registerLoan` y `LoanDAO.registerReturn` desactivan el auto-commit, ejecutan dos operaciones (insertar/marcar devuelto y actualizar disponibilidad) y hacen `commit`/`rollback` segun corresponda, garantizando que no queden datos inconsistentes.

## Filtro de autenticacion

`AuthenticationFilter` intercepta todas las peticiones. Permite el acceso publico solo a `/login`, `/logout`, `/`, `/index.jsp` y archivos estaticos (`/css/...`, `/js/...`, `/images/...`). Si no hay sesion activa, redirige al login.
