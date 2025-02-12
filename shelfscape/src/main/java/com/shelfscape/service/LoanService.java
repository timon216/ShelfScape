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

    public Loan saveLoan(Loan loan) {
        if (loan.getStatus() == LoanStatus.RESERVED) {
            // Set the reservation expiry date to 7 days from the loan date
            loan.setReservationExpiryDate(loan.getLoanDate().plusDays(7));
        }
        return loanRepository.save(loan);
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    // Keep only one definition of this method
    @Scheduled(cron = "0 0 0 * * ?")  // This will run at midnight every day
    public void checkAndUpdateExpiredLoans() {
        List<Loan> loans = loanRepository.findAll();

        for (Loan loan : loans) {
            if (loan.getStatus() == LoanStatus.RESERVED && loan.getReservationExpiryDate().isBefore(LocalDate.now())) {
                // If the reservation has expired, mark it as OVERDUE
                loan.setStatus(LoanStatus.OVERDUE);
                loanRepository.save(loan);
            }
        }
    }
}
