package cl.untec.library.model;

import java.time.LocalDate;

public class Loan {

  private Long id;
  private Long userId;
  private Long bookId;
  private LocalDate loanDate;
  private LocalDate returnDate;
  private boolean returned;
  private String userName;
  private String bookTitle;

  public Loan() {}

  public Loan(
    Long id,
    Long userId,
    Long bookId,
    LocalDate loanDate,
    LocalDate returnDate,
    boolean returned
  ) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.loanDate = loanDate;
    this.returnDate = returnDate;
    this.returned = returned;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public LocalDate getLoanDate() {
    return loanDate;
  }

  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public boolean isReturned() {
    return returned;
  }

  public void setReturned(boolean returned) {
    this.returned = returned;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }
}
