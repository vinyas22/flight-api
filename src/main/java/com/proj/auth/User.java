package com.proj.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    private String username;
    private String password;
    private String email;
    private Long userId; // Add this field
    private String role;

    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // getters and setters

    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }

    public String getEmail() { return email; }    // Add getter
    public void setEmail(String e) { email = e; }  // Add setter
}
