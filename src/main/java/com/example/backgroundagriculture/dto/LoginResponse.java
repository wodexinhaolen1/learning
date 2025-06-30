package com.example.backgroundagriculture.dto;

import com.example.backgroundagriculture.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String email;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}