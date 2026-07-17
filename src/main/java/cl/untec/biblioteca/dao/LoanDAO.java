package cl.untec.biblioteca.dao;

import cl.untec.biblioteca.model.Loan;
import cl.untec.biblioteca.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de la tabla loan.
 *
 * Las operaciones registerLoan y registerReturn se ejecutan
 * dentro de una transaccion JDBC para evitar estados intermedios
 * (por ejemplo, que quede registrado un prestamo sin bajar la
 * disponibilidad del libro).
 */
public class LoanDAO {

    /** DAO auxiliar para consultar y actualizar el estado de los libros sin salir de la transaccion. */
    private final BookDAO bookDAO = new BookDAO();

    /** Consulta SQL para listar todos los prestamos con el nombre del usuario y el titulo del libro, ordenados por fecha descendente. */
    private static final String SQL_FIND_ALL = "SELECT l.id, l.user_id, l.book_id, l.loan_date, l.return_date, l.returned, "
            +
            "       u.name AS user_name, b.title AS book_title " +
            "FROM loan l " +
            "INNER JOIN app_user u ON u.id = l.user_id " +
            "INNER JOIN book   b ON b.id = l.book_id " +
            "ORDER BY l.loan_date DESC, l.id DESC";

    /** Consulta SQL para listar los prestamos de un usuario concreto, con joins para mostrar datos legibles. */
    private static final String SQL_FIND_BY_USER = "SELECT l.id, l.user_id, l.book_id, l.loan_date, l.return_date, l.returned, "
            +
            "       u.name AS user_name, b.title AS book_title " +
            "FROM loan l " +
            "INNER JOIN app_user u ON u.id = l.user_id " +
            "INNER JOIN book   b ON b.id = l.book_id " +
            "WHERE l.user_id = ? " +
            "ORDER BY l.loan_date DESC, l.id DESC";

    /** Consulta SQL basica para buscar un prestamo por su identificador (sin joins). */
    private static final String SQL_FIND_BY_ID = "SELECT id, user_id, book_id, loan_date, return_date, returned " +
            "FROM loan WHERE id = ?";

    /** Consulta SQL para insertar un nuevo prestamo, con devolucion nula y flag returned en FALSE. */
    private static final String SQL_INSERT = "INSERT INTO loan (user_id, book_id, loan_date, return_date, returned) " +
            "VALUES (?, ?, ?, NULL, FALSE)";

    /** Consulta SQL para marcar un prestamo como devuelto, asignando la fecha actual. */
    private static final String SQL_UPDATE_RETURN = "UPDATE loan SET return_date = ?, returned = TRUE WHERE id = ?";

    /**
     * Lista todos los prestamos del sistema con datos de join (nombre del
     * usuario y titulo del libro), ordenados por fecha de prestamo descendente.
     *
     * @return lista de prestamos, posiblemente vacia
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                loans.add(mapWithJoin(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar prestamos", ex);
        }
        return loans;
    }

    /**
     * Lista los prestamos de un usuario concreto, con datos de join.
     *
     * @param userId id del usuario a consultar
     * @return lista de prestamos del usuario, posiblemente vacia
     * @throws RuntimeException si ocurre un error de SQL al consultar la base
     */
    public List<Loan> findByUser(Long userId) {
        List<Loan> loans = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_USER)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapWithJoin(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar prestamos del usuario", ex);
        }
        return loans;
    }

    /**
     * Registra un prestamo verificando disponibilidad y dejando
     * el libro como no disponible, todo dentro de una transaccion.
     *
     * @param userId id del usuario que solicita el prestamo
     * @param bookId id del libro a prestar
     * @return true si el prestamo se realizo, false si el libro no estaba disponible o no existe
     * @throws RuntimeException si ocurre un error de SQL no controlado durante la transaccion
     */
    public boolean registerLoan(Long userId, Long bookId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1) Verificar que el libro este disponible.
                var bookOpt = bookDAO.findById(bookId);
                if (bookOpt.isEmpty() || !bookOpt.get().isAvailable()) {
                    connection.rollback();
                    return false;
                }

                // 2) Insertar el prestamo.
                try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
                    statement.setLong(1, userId);
                    statement.setLong(2, bookId);
                    statement.setDate(3, Date.valueOf(LocalDate.now()));
                    statement.executeUpdate();
                }

                // 3) Marcar el libro como no disponible.
                bookDAO.updateAvailability(connection, bookId, false);

                connection.commit();
                return true;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al registrar prestamo", ex);
        }
    }

    /**
     * Registra la devolucion de un prestamo existente y vuelve a
     * dejar disponible el libro asociado, todo dentro de una transaccion.
     *
     * @param loanId id del prestamo a devolver
     * @return true si la devolucion se realizo, false si el prestamo no existe o ya estaba devuelto
     * @throws RuntimeException si ocurre un error de SQL no controlado durante la transaccion
     */
    public boolean registerReturn(Long loanId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Loan loan = null;
                try (PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
                    statement.setLong(1, loanId);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            loan = mapBasic(rs);
                        }
                    }
                }
                if (loan == null || loan.isReturned()) {
                    connection.rollback();
                    return false;
                }

                try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_RETURN)) {
                    statement.setDate(1, Date.valueOf(LocalDate.now()));
                    statement.setLong(2, loanId);
                    statement.executeUpdate();
                }

                bookDAO.updateAvailability(connection, loan.getBookId(), true);

                connection.commit();
                return true;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al registrar devolucion", ex);
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un Loan incluyendo los
     * campos auxiliares de join (user_name, book_title).
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return instancia de Loan con los datos basicos y auxiliares
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private Loan mapWithJoin(ResultSet rs) throws SQLException {
        Loan loan = mapBasic(rs);
        loan.setUserName(rs.getString("user_name"));
        loan.setBookTitle(rs.getString("book_title"));
        return loan;
    }

    /**
     * Convierte la fila actual del ResultSet en un Loan con los
     * unicos campos de la tabla loan (sin joins).
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return instancia de Loan basica
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private Loan mapBasic(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getLong("id"));
        loan.setUserId(rs.getLong("user_id"));
        loan.setBookId(rs.getLong("book_id"));
        Date loanDate = rs.getDate("loan_date");
        if (loanDate != null) {
            loan.setLoanDate(loanDate.toLocalDate());
        }
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }
        loan.setReturned(rs.getBoolean("returned"));
        return loan;
    }
}
