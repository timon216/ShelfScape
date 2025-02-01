package com.shelfscape;

import com.shelfscape.model.Role;
import com.shelfscape.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

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
    }
}
