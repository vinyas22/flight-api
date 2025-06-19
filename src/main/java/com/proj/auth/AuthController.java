package com.proj.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.proj.controller.EmailService;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

@Autowired
private UserService userService;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    User user = userRepository.findByUsername(request.getUsername());
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
    }
    String storedPassword = user.getPassword();
    String rawPassword = request.getPassword();

    boolean passwordMatches;

    if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
        passwordMatches = passwordEncoder.matches(rawPassword, storedPassword);
    } else {
        passwordMatches = storedPassword.equals(rawPassword);
        if (passwordMatches) {
            userService.updatePasswordToHashed(user, rawPassword);  // migrate password here
        }
    }

    if (!passwordMatches) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
    }

    String token = jwtUtil.generateToken(user.getUsername());

    Map<String, Object> response = new HashMap<>();
    response.put("token", token);
    response.put("username", user.getUsername());
    response.put("user_id", user.getUserId());  // Assuming getUserId() exists and returns Long
    response.put("role", user.getRole());       // If you have role info, else omit or set default
    return ResponseEntity.ok(response);
}

@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    Map<String, Object> response = new HashMap<>();

    if (userRepository.findByUsername(user.getUsername()) != null) {
        response.put("success", false);
        response.put("message", "Username already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    response.put("success", true);
    response.put("message", "User registered successfully");
    return ResponseEntity.ok(response);
}

@PostMapping("/request-password-reset")
public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
    String username = request.get("username");

    Map<String, Object> response = new HashMap<>();

    if (username == null || username.trim().isEmpty()) {
        response.put("success", false);
        response.put("message", "Username is required");
        return ResponseEntity.badRequest().body(response);
    }

    String email;
    try {
        email = jdbcTemplate.queryForObject(
            "SELECT email FROM users WHERE username = ?", String.class, username);
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "User not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    if (email == null || email.trim().isEmpty()) {
        response.put("success", false);
        response.put("message", "User email not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        response.put("success", false);
        response.put("message", "Invalid email address");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    String token = UUID.randomUUID().toString();
    Timestamp expiry = new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000);

    jdbcTemplate.update(
        "INSERT INTO password_reset_tokens (username, token, expiry) VALUES (?, ?, ?)",
        username, token, expiry);

    String resetLink = "http://localhost:4200/reset-password?token=" + token;

    emailService.sendSimpleEmail(
        email,
        "Password Reset Request",
        "To reset your password, click the link below:\n" + resetLink);

    response.put("success", true);
    response.put("message", "Password reset email sent.");
    return ResponseEntity.ok(response);
}

@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
    String token = request.get("token");
    String newPassword = request.get("newPassword");

    Map<String, Object> response = new HashMap<>();

    if (token == null || newPassword == null) {
        response.put("success", false);
        response.put("message", "Missing token or password");
        return ResponseEntity.badRequest().body(response);
    }

    List<Map<String, Object>> tokens = jdbcTemplate.queryForList(
        "SELECT username, expiry FROM password_reset_tokens WHERE token = ?", token);

    if (tokens.isEmpty()) {
        response.put("success", false);
        response.put("message", "Invalid token");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Map<String, Object> tokenData = tokens.get(0);
    Timestamp expiry = (Timestamp) tokenData.get("expiry");

    if (expiry.before(new Timestamp(System.currentTimeMillis()))) {
        response.put("success", false);
        response.put("message", "Token expired");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    String username = (String) tokenData.get("username");
    String encodedPassword = passwordEncoder.encode(newPassword);

    jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?", encodedPassword, username);
    int rowsDeleted = jdbcTemplate.update("DELETE FROM password_reset_tokens WHERE token = ?", token);

    System.out.println("Deleted rows from password_reset_tokens: " + rowsDeleted);

    response.put("success", true);
    response.put("message", "Password reset successful.");
    return ResponseEntity.ok(response);
}

 
 @GetMapping("/test-token")
    public String testToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null) {
                return "✅ Token is valid. User: " + username;
            } else {
                return "❌ Token is invalid or expired.";
            }
        } else {
            return "⚠️ Authorization header missing or malformed.";
        }
    }

}
