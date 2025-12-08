package com.lsptddi.silsp.security;

import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.model.Role;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private UserRepository userRepository;

    // @Override
    // @Transactional
    // public UserDetails loadUserByUsername(String input) throws
    // UsernameNotFoundException {
    // // LOGIKA BARU:
    // // Input dari form login (bisa berupa username 'satria' atau email
    // 'satria@gmail.com')
    // // Kita kirim input yang sama ke kedua parameter.
    // // Artinya: "Cari user yang username-nya 'input' ATAU email-nya 'input'"

    // User user = userRepository.findByUsernameOrEmail(input, input)
    // .orElseThrow(() -> new UsernameNotFoundException("Username atau Email tidak
    // ditemukan: " + input));

    // // Mapping User Database ke User Spring Security (Tetap sama)
    // return new org.springframework.security.core.userdetails.User(
    // user.getUsername(),
    // user.getPassword(),
    // user.getAuthorities() // Pastikan method getAuthorities() atau mapping role
    // Anda sudah benar di sini
    // );
    // }

    // @Override
    // @Transactional
    // public UserDetails loadUserByUsername(String input) throws
    // UsernameNotFoundException {
    // // LOGIKA KUNCI: Cari berdasarkan Username ATAU Email
    // // Pastikan method findByUsernameOrEmail ada di UserRepository
    // User user = userRepository.findByUsernameOrEmail(input, input)
    // .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan"));

    // // Debugging (Opsional: Cek di console server apakah user ketemu)
    // // System.out.println("User ditemukan: " + user.getUsername());

    // return new org.springframework.security.core.userdetails.User(
    // user.getUsername(),
    // user.getPassword(),
    // user.getRoles().stream()
    // .map(role -> new SimpleGrantedAuthority(role.getName()))
    // .collect(Collectors.toList())
    // );
    // }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // Cari User berdasarkan Username ATAU Email (input yang sama dikirim ke kedua
        // parameter)
        User user = userRepository.findByUsernameOrEmail(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("Username atau Email tidak ditemukan: " + input));

        // ... sisa kode mapping user ...
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Spring Security tetap butuh username asli untuk session
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    // Konversi Role Database ke Authority Spring Security
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // misal: "ADMIN"
                .collect(Collectors.toList());
    }

    // @Override
    // @Transactional // Penting agar bisa load Role (Lazy Loading)
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException {
    // // 1. Cari User di Database
    // User user = userRepository.findByUsername(username)
    // .orElseThrow(() -> new UsernameNotFoundException("Username atau Password
    // salah"));

    // // 2. Mapping User Database ke User Spring Security
    // return new org.springframework.security.core.userdetails.User(
    // user.getUsername(),
    // user.getPassword(),
    // mapRolesToAuthorities(user.getRoles())
    // );
    // }

}
