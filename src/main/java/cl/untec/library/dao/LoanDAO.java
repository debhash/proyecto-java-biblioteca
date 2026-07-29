package cl.untec.library.dao;

import cl.untec.library.model.Loan;
import cl.untec.library.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

  private final BookDAO bookDAO = new BookDAO();

  private static final String SQL_FIND_ALL =
    "SELECT l.id, l.user_id, l.book_id, l.loan_date, l.return_date, l.returned, u.name AS user_name, b.title AS book_title FROM loan l INNER JOIN app_user u ON u.id = l.user_id INNER JOIN book b ON b.id = l.book_id ORDER BY l.loan_date DESC, l.id DESC";
  private static final String SQL_FIND_BY_USER =
    "SELECT l.id, l.user_id, l.book_id, l.loan_date, l.return_date, l.returned, u.name AS user_name, b.title AS book_title FROM loan l INNER JOIN app_user u ON u.id = l.user_id INNER JOIN book b ON b.id = l.book_id WHERE l.user_id = ? ORDER BY l.loan_date DESC, l.id DESC";
  private static final String SQL_FIND_BY_ID =
    "SELECT id, user_id, book_id, loan_date, return_date, returned FROM loan WHERE id = ?";
  private static final String SQL_INSERT =
    "INSERT INTO loan (user_id, book_id, loan_date, return_date, returned) VALUES (?, ?, ?, NULL, FALSE)";
  private static final String SQL_UPDATE_RETURN =
    "UPDATE loan SET return_date = ?, returned = TRUE WHERE id = ?";

  public List<Loan> findAll() {
    List<Loan> loans = new ArrayList<>();
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        loans.add(mapWithJoin(resultSet));
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al listar prestamos.", ex);
    }
    return loans;
  }

  public List<Loan> findByUser(Long userId) {
    List<Loan> loans = new ArrayList<>();
    try (
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(
        SQL_FIND_BY_USER
      )
    ) {
      statement.setLong(1, userId);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          loans.add(mapWithJoin(resultSet));
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Error al listar prestamos del usuario.", ex);
    }
    return loans;
  }

  public boolean registerLoan(Long userId, Long bookId) {
    try (Connection connection = DatabaseConnection.getConnection()) {
      connection.setAutoCommit(false);
      try {
        var bookOpt = bookDAO.findById(bookId);
        if (bookOpt.isEmpty() || !bookOpt.get().isAvailable()) {
          connection.rollback();
          return false;
        }

        try (
          PreparedStatement statement = connection.prepareStatement(SQL_INSERT)
        ) {
          statement.setLong(1, userId);
          statement.setLong(2, bookId);
          statement.setDate(3, Date.valueOf(LocalDate.now()));
          statement.executeUpdate();
        }

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
      throw new RuntimeException("Error al registrar prestamo.", ex);
    }
  }

  public boolean registerReturn(Long loanId) {
    try (Connection connection = DatabaseConnection.getConnection()) {
      connection.setAutoCommit(false);
      try {
        Loan loan = null;
        try (
          PreparedStatement statement = connection.prepareStatement(
            SQL_FIND_BY_ID
          )
        ) {
          statement.setLong(1, loanId);
          try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
              loan = mapBasic(resultSet);
            }
          }
        }

        if (loan == null || loan.isReturned()) {
          connection.rollback();
          return false;
        }

        try (
          PreparedStatement statement = connection.prepareStatement(
            SQL_UPDATE_RETURN
          )
        ) {
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
      throw new RuntimeException("Error al registrar devolucion.", ex);
    }
  }

  private Loan mapWithJoin(ResultSet resultSet) throws SQLException {
    Loan loan = mapBasic(resultSet);
    loan.setUserName(resultSet.getString("user_name"));
    loan.setBookTitle(resultSet.getString("book_title"));
    return loan;
  }

  private Loan mapBasic(ResultSet resultSet) throws SQLException {
    Loan loan = new Loan();
    loan.setId(resultSet.getLong("id"));
    loan.setUserId(resultSet.getLong("user_id"));
    loan.setBookId(resultSet.getLong("book_id"));
    Date loanDate = resultSet.getDate("loan_date");
    if (loanDate != null) {
      loan.setLoanDate(loanDate.toLocalDate());
    }
    Date returnDate = resultSet.getDate("return_date");
    if (returnDate != null) {
      loan.setReturnDate(returnDate.toLocalDate());
    }
    loan.setReturned(resultSet.getBoolean("returned"));
    return loan;
  }
}
