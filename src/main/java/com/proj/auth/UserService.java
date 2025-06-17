package com.proj.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void updatePasswordToHashed(User user, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?", hashed, user.getUsername());
        user.setPassword(hashed);  // update user object as well
    }
}
