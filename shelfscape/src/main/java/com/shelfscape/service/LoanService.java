package com.shelfscape.service;

import com.shelfscape.model.Book;
import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.repository.BookRepository;
import com.shelfscape.repository.LoanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getLoansByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public Loan saveLoan(Loan loan) {
        if (loan.getStatus() == LoanStatus.RESERVED) {
            loan.setReservationExpiryDate(loan.getLoanDate().plusDays(7));
        }
        return loanRepository.save(loan);
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    public List<Loan> searchLoans(String query) {
        return loanRepository.findByBookTitleContainingIgnoreCaseOrBookAuthorContainingIgnoreCaseOrBookIsbnContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(query, query, query, query, query);
    }

    public Loan updateLoanStatus(Long loanId, LoanStatus status) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalStateException("Loan not found with ID: " + loanId));

        if (status == LoanStatus.BORROWED && loan.getStatus() == LoanStatus.RESERVED) {
            // Change status to borrowed and extend the loan date by 30 days
            loan.setStatus(LoanStatus.BORROWED);
            loan.setLoanDate(LocalDate.now());
            loan.setReturnDate(LocalDate.now().plusDays(30));
        } else if (status == LoanStatus.RETURNED && loan.getStatus() == LoanStatus.BORROWED) {
            // Change status to returned and make the book available again
            loan.setStatus(LoanStatus.RETURNED);
            Book book = loan.getBook();
            book.setAvailable(true);
            bookRepository.save(book);
        }

        return loanRepository.save(loan);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void checkAndUpdateExpiredLoans() {
        List<Loan> reservedLoans = loanRepository.findByStatus(LoanStatus.RESERVED);
        LocalDate today = LocalDate.now();

        reservedLoans.stream()
                .filter(loan -> loan.getReservationExpiryDate().isBefore(today))
                .forEach(loan -> {
                    loan.setStatus(LoanStatus.RESERVATION_EXPIRED);
                    loanRepository.save(loan);
                });
    }

    public Loan removeReservation(Long loanId, Long userId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null && loan.getUser().getId().equals(userId) && loan.getStatus() == LoanStatus.RESERVED) {

            Book book = loan.getBook();
            book.setAvailable(true);
            bookRepository.save(book);

            loanRepository.delete(loan);
            return loan;
        }
        return null;
    }

    public Loan extendLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalStateException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RESERVED) {
            loan.setReservationExpiryDate(loan.getReservationExpiryDate().plusDays(7));
        } else if (loan.getStatus() == LoanStatus.BORROWED) {
            loan.setReturnDate(loan.getReturnDate().plusDays(7));
        }

        return loanRepository.save(loan);
    }
}
