-- Datos iniciales de la Biblioteca Digital UNTEC.
-- Un Admin y un estudiante de prueba.
--
-- Admin
INSERT INTO
  app_user (name, email, password, role)
VALUES
  (
    'Luis Berrios',
    'luis@untec.cl',
    'admin123',
    'LIBRARIAN'
  );

-- Estudiante
INSERT INTO
  app_user (name, email, password, role)
VALUES
  (
    'Bruce Lee',
    'bruce@untec.cl',
    'estudiante123',
    'STUDENT'
  );

-- Tres libros de ejemplo para demostrar el catalogo y los prestamos.
INSERT INTO
  book (title, author, isbn, available)
VALUES
  (
    'Hail Mary',
    'Andy Weir',
    '9788413148465',
    TRUE
  );

INSERT INTO
  book (title, author, isbn, available)
VALUES
  (
    'Frankenstein',
    'Mary Shelley',
    '9780192815323',
    TRUE
  );

INSERT INTO
  book (title, author, isbn, available)
VALUES
  (
    'Kafka on the Shore',
    'Haruki Murakami',
    '9780307275264',
    TRUE
  );