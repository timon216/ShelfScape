package com.shelfscape.repository;

import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserId(Long userId);
    Loan findByIdAndUserId(Long loanId, Long userId);

    List<Loan> findByBookTitleContainingIgnoreCaseOrBookAuthorContainingIgnoreCaseOrBookIsbnContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(
            String title, String author, String isbn, String userfirstname, String userlastname);

    long countByUserAndStatus(User user, LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.user.id = ?1 AND l.book.isbn = ?2 AND l.status = ?3")
    Optional<Loan> findByUserIdAndBookIsbnAndStatus(Long userId, String isbn, LoanStatus status);
}
