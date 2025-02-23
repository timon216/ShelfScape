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

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

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

        if (!userService.canReserveOrBorrow(user)) {
            throw new RuntimeException("You can only reserve or borrow a maximum of 6 books at the same time.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getQuantity() <= 0) {
            throw new RuntimeException("No available copies of this book.");
        }

        // Check if the user already has a reservation for this ISBN
        if (loanService.hasReservationForIsbn(user.getId(), book.getIsbn())) {
            throw new RuntimeException("You cannot reserve the same book more than once.");
        }

        book.setQuantity(book.getQuantity() - 1); // Decrement quantity
        bookRepository.save(book);

        // Create a new loan for the reservation
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(LoanStatus.RESERVED);

        // Set reservation expiry date (7 days from now)
        loan.setReservationExpiryDate(LocalDate.now().plusDays(7));

        loanRepository.save(loan);
    }

    public void returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1); // Increment quantity
        bookRepository.save(book);

        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);
    }

    public List<Book> filterBooksByGenres(List<String> genres) {
        return bookRepository.findByGenreIn(genres);
    }

    public List<Book> searchAndFilterByGenres(String searchQuery, List<String> genres) {
        if (searchQuery != null && !searchQuery.isBlank() && genres != null && !genres.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCaseAndGenreIn(
                    searchQuery, searchQuery, searchQuery, genres);
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                    searchQuery, searchQuery, searchQuery);
        }

        if (genres != null && !genres.isEmpty()) {
            return bookRepository.findByGenreIn(genres);
        }

        return bookRepository.findAll();
    }

    public List<String> getAllGenres() {
        return bookRepository.findDistinctGenres();
    }
}
