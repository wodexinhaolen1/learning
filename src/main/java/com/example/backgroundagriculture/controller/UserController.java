package com.example.backgroundagriculture.controller;

import com.example.backgroundagriculture.dto.LoginRequest;
import com.example.backgroundagriculture.dto.LoginResponse;
import com.example.backgroundagriculture.dto.ResetPasswordRequest;
import com.example.backgroundagriculture.dto.UserRegisterRequest;
import com.example.backgroundagriculture.entity.User;
import com.example.backgroundagriculture.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    @Operation(summary = "验证用户名和邮箱是否匹配")
    public ResponseEntity<Map<String, Boolean>> verifyUserEmail(@Valid @RequestBody Map<String, String> request) {
        boolean isValid = userService.verifyUserEmail(request.get("username"), request.get("email"));
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getUsername(), request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "获取所有用户")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除用户")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegisterRequest request) {
        User user = new User();
        user.setId(id);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @GetMapping("/check/{username}")
    @Operation(summary = "检查用户名是否存在")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }
}