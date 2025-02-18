package com.shelfscape;

import com.shelfscape.model.Role;
import com.shelfscape.service.BookImportService;
import com.shelfscape.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private BookImportService bookImportService;

    private final RoleService roleService;

    public DataLoader(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if USER role exists, if not create it
        if (roleService.getRoleByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleService.saveRole(userRole);
        }

        // Check if ADMIN role exists, if not create it
        if (roleService.getRoleByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleService.saveRole(adminRole);
        }

        try {
            bookImportService.importBooksFromCSV();
            System.out.println("Books have been imported successfully.");
        } catch (IOException e) {
            System.err.println("Error importing books: " + e.getMessage());
        }
    }
}
