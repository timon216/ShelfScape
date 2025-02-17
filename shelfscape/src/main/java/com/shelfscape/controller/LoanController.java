//package com.shelfscape.controller;
//
//import com.shelfscape.model.Loan;
//import com.shelfscape.model.LoanStatus;
//import com.shelfscape.service.LoanService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/admin/loans")
//public class LoanController {
//
//    @Autowired
//    private LoanService loanService;
//
//    @GetMapping("/{id}")
//    public Optional<Loan> getLoanById(@PathVariable Long id) {
//        return loanService.getLoanById(id);
//    }
//
//    @PostMapping
//    public Loan addLoan(@RequestBody Loan loan) {
//        return loanService.saveLoan(loan);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteLoan(@PathVariable Long id) {
//        loanService.deleteLoan(id);
//    }
//
//    // New endpoint for getting overdue loans
//    @GetMapping("/overdue")
//    public List<Loan> getOverdueLoans() {
//        List<Loan> loans = loanService.getAllLoans();
//        return loans.stream()
//                .filter(loan -> loan.getStatus() == LoanStatus.OVERDUE)
//                .toList();
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/loans")
//    public String manageLoans(Model model) {
//        List<Loan> loans = loanService.getAllLoans();
//        model.addAttribute("loans", loans);
//        return "admin/loans";
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping
//    public String getAllLoans(Model model) {
//        List<Loan> loans = loanService.getAllLoans();
//        model.addAttribute("loans", loans);
//        return "admin/loans";
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PatchMapping("/{id}/status")
//    public String updateLoanStatus(@PathVariable Long id, @RequestParam LoanStatus status, Model model) {
//        Optional<Loan> optionalLoan = loanService.getLoanById(id);
//        if (optionalLoan.isPresent()) {
//            Loan loan = optionalLoan.get();
//            loan.setStatus(status);
//            loanService.saveLoan(loan);
//        }
//        return "redirect:/admin/loans";
//    }
//}
