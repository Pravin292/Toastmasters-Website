package com.rathinam.toastmasters.modules.auth.dto;

import com.rathinam.toastmasters.modules.account.entity.AccountRole;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private AccountRole role;

    public AuthResponse(String token, String email, AccountRole role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }
}
