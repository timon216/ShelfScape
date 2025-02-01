package com.shelfscape.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Scanner;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "abc";  // Hasło, które chcesz haszować

        // Generowanie zahaszowanego hasła
        String hashedPassword = encoder.encode(rawPassword);

        // Wydrukowanie obu wersji
        System.out.println("Plaintext password: " + rawPassword);  // Hasło do logowania
        System.out.println("Hashed password: " + hashedPassword);  // Hasło do bazy danych

        // Tworzymy obiekt Scanner do wprowadzenia hasła przez użytkownika
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nPlease enter the password to check:");
        String enteredPassword = scanner.nextLine();  // Wprowadzenie hasła

        // Sprawdzamy, czy hasło pasuje do przechowywanego hasha
        boolean passwordMatches = encoder.matches(enteredPassword, hashedPassword);

        // Drukujemy wynik
        if (passwordMatches) {
            System.out.println("Password is correct!");
        } else {
            System.out.println("Password is incorrect.");
        }
    }
}
