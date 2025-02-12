package com.shelfscape.service;

import com.shelfscape.model.Book;
import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.model.User;
import com.shelfscape.repository.BookRepository;
import com.shelfscape.repository.LoanRepository;
import com.shelfscape.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(query, query, query);
    }

    public void reserveBook(Long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is already reserved.");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        // Create a new loan for the reservation
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(LoanStatus.RESERVED);

        // Setting reservation expiry date (7 days from now)
        loan.setReservationExpiryDate(LocalDate.now().plusDays(7));

        loanRepository.save(loan);
    }


}
