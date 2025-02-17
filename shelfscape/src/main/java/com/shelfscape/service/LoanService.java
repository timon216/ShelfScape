package com.shelfscape.service;

import com.shelfscape.model.Book;
import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.repository.LoanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
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
        loan.setStatus(status);
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
}
