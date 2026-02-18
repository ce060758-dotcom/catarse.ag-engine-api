package com.catarse.engine.user.service;

import com.catarse.engine.user.dto.request.UserRequest;
import com.catarse.engine.user.dto.request.LoginRequest;
import com.catarse.engine.user.dto.response.UserResponse;
import com.catarse.engine.user.dto.response.JwtResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    // Auth
    JwtResponse authenticate(LoginRequest loginRequest);

    // User CRUD
    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    // Role management
    UserResponse updateUserRole(Long id, String role);

    // Password
    void changePassword(Long id, String oldPassword, String newPassword);
}