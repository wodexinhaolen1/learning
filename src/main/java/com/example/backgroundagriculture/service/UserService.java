package com.example.backgroundagriculture.service;

import com.example.backgroundagriculture.dto.LoginRequest;
import com.example.backgroundagriculture.dto.LoginResponse;
import com.example.backgroundagriculture.dto.UserRegisterRequest;
import com.example.backgroundagriculture.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // 用户登录
    LoginResponse login(LoginRequest request);

    // 用户注册
    void register(UserRegisterRequest request);

    // 根据用户名查询用户
    User findByUsername(String username);

    User createUser(User user);
    
    List<User> getAllUsers();
    
    Optional<User> getUserById(Long id);
    
    void deleteUser(Long id);
    
    User updateUser(User user);

    boolean verifyUserEmail(String username, String email);

    void resetPassword(String username, String email, String newPassword);
}