package com.shelfscape.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private String isbn;

    // Remove the 'available' field
    // private boolean available;

    // Add the 'quantity' field
    private int quantity;

    @OneToMany(mappedBy = "book")
    private Set<Loan> loans;

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Remove the 'available' getter and setter
    // public boolean isAvailable() {
    //     return available;
    // }
    // public void setAvailable(boolean available) {
    //     this.available = available;
    // }

    // Add the 'quantity' getter and setter
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Set<Loan> getLoans() {
        return loans;
    }
    public void setLoans(Set<Loan> loans) {
        this.loans = loans;
    }
}
