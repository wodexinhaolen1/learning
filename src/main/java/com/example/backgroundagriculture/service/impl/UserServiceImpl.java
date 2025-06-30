package com.example.backgroundagriculture.service.impl;

import com.example.backgroundagriculture.dto.LoginRequest;
import com.example.backgroundagriculture.dto.LoginResponse;
import com.example.backgroundagriculture.dto.UserRegisterRequest;
import com.example.backgroundagriculture.entity.User;
import com.example.backgroundagriculture.exception.BusinessException;
import com.example.backgroundagriculture.repository.UserRepository;
import com.example.backgroundagriculture.service.UserService;
import com.example.backgroundagriculture.service.VisitorStatisticsService;
import com.example.backgroundagriculture.util.JwtUtil;
import com.example.backgroundagriculture.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VisitorStatisticsService visitorStatisticsService;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 记录访问统计
        visitorStatisticsService.addVisitor();
        visitorStatisticsService.addActiveUser(user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user);
    }

    @Override
    public void register(UserRegisterRequest request) {
        // 验证密码格式
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            throw new BusinessException("密码必须包含数字和英文字母");
        }

        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("邮箱已被注册");
        }
        // 验证密码规则
        if (!ValidationUtil.isValidPassword(user.getPassword())) {
            throw new BusinessException(ValidationUtil.PASSWORD_RULE_MESSAGE);
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        // 如果修改了邮箱，检查新邮箱是否已存在
        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("邮箱已被注册");
        }

        // 如果密码不为空，则更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }

        return userRepository.save(user);
    }

    @Override
    public boolean verifyUserEmail(String username, String email) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BusinessException("用户不存在");
        }
        return userOpt.get().getEmail().equals(email);
    }

    @Override
    @Transactional
    public void resetPassword(String username, String email, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!user.getEmail().equals(email)) {
            throw new BusinessException("邮箱验证失败");
        }

        // 验证新密码规则
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new BusinessException(ValidationUtil.PASSWORD_RULE_MESSAGE);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}