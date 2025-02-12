package com.shelfscape.service;

import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

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

    public Loan updateLoanStatus(Long loanId, LoanStatus status) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        loan.setStatus(status);
        return loanRepository.save(loan);
    }


    @Scheduled(cron = "0 0 0 * * ?")  // Runs at midnight every day
    public void checkAndUpdateExpiredLoans() {
        List<Loan> loans = loanRepository.findAll();

        for (Loan loan : loans) {
            if (loan.getStatus() == LoanStatus.RESERVED && loan.getReservationExpiryDate().isBefore(LocalDate.now())) {
                loan.setStatus(LoanStatus.RESERVATION_EXPIRED);
                loanRepository.save(loan);
            }
        }
    }
}
