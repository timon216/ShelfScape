package com.shelfscape.service;

import com.shelfscape.model.Book;
import com.shelfscape.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookImportService {

    @Autowired
    private BookRepository bookRepository;

    // Path to the CSV file (from classpath)
    private static final String CSV_FILE_PATH = "books.csv";

    // Method to import books from the CSV file
    public void importBooksFromCSV() throws IOException {
        Set<String> existingIsbns = new HashSet<>();
        List<Book> existingBooks = bookRepository.findAll();
        for (Book book : existingBooks) {
            existingIsbns.add(book.getIsbn());
        }

        // Load the CSV file from the classpath
        ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line;

        // Skip header line
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            String isbn = data[3];

            // Skip if the book with this ISBN already exists in the database
            if (!existingIsbns.contains(isbn)) {
                Book book = new Book();
                book.setTitle(data[0]);
                book.setAuthor(data[1]);
                book.setGenre(data[2]);
                book.setIsbn(isbn);
                book.setQuantity(Integer.parseInt(data[4])); // Set the quantity
                bookRepository.save(book);
            }
        }
    }
}
