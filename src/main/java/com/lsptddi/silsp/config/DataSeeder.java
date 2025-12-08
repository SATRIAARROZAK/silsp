// package com.lsptddi.silsp.config;

// import com.lsptddi.silsp.model.Role;
// import com.lsptddi.silsp.model.User;
// import com.lsptddi.silsp.repository.RoleRepository;
// import com.lsptddi.silsp.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import java.util.Collections;
// import java.util.HashSet;

// @Component
// public class DataSeeder implements CommandLineRunner {

//     @Autowired private UserRepository userRepository;
//     @Autowired private RoleRepository roleRepository;
//     @Autowired private PasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) throws Exception {
//         // Cek apakah admin sudah ada?
//         if (userRepository.findByUsername("admin").isEmpty()) {
            
//             // 1. Buat Role Admin jika belum ada
//             Role adminRole = roleRepository.findByName("Admin").orElseGet(() -> {
//                 Role newRole = new Role();
//                 newRole.setName("Admin");
//                 return roleRepository.save(newRole);
//             });

//             // 2. Buat User Admin
//             User admin = new User();
//             admin.setUsername("admin");
//             admin.setEmail("admin@gmail.com");
//             admin.setFullName("Administrator Sistem");
//             // PENTING: Password di-hash disini
//             admin.setPassword(passwordEncoder.encode("admin123")); 
//             admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));

//             userRepository.save(admin);
//             System.out.println(">>> USER ADMIN BERHASIL DIBUAT (Pass: 123) <<<");
//         }
//     }
// }