package com.shelfscape.repository;

import com.shelfscape.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByEmailContainingOrFirstNameContainingOrLastNameContaining(String email, String firstName, String lastName);
}
