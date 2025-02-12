package com.shelfscape.repository;

import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserId(Long userId);
}
