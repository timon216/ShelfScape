package com.shelfscape.repository;

import com.shelfscape.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    void deleteById(Long id);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String title, String author, String isbn);

    List<Book> findByGenreIn(List<String> genres);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCaseAndGenreIn(
            String title, String author, String isbn, List<String> genres);

    @Query("SELECT DISTINCT b.genre FROM Book b")
    List<String> findDistinctGenres();
}

