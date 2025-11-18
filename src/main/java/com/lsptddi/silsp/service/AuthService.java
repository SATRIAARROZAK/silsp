package com.lsptddi.silsp.service;

import com.lsptddi.silsp.model.Role;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.RoleRepository;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerAsesi(String username, String email, String password) {
        // 1. Validasi Duplikasi
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username sudah digunakan");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email sudah digunakan");
        }

        // 2. Cari Role 'Asesi', jika belum ada di DB, buat baru (untuk setup awal)
        Role roleAsesi = roleRepository.findByName("Asesi");
        if (roleAsesi == null) {
            roleAsesi = new Role("Asesi");
            roleRepository.save(roleAsesi);
        }

        // 3. Buat User Baru
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        // Enkripsi password sebelum simpan ke DB
        newUser.setPassword(passwordEncoder.encode(password));
        
        // 4. SET ROLE OTOMATIS KE ASESI (Logic Inti)
        newUser.setRole(roleAsesi);

        // 5. Simpan ke Database
        userRepository.save(newUser);
    }
}