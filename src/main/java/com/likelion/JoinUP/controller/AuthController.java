package com.likelion.JoinUP.controller;

import com.likelion.JoinUP.dto.ApiResponse;
import com.likelion.JoinUP.dto.UserDTO;
import com.likelion.JoinUP.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/verification")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody @Valid UserDTO.VerificationRequest request) {
        if (userService.verifyEmail(request.getEmail())) {
            return ResponseEntity.ok(new ApiResponse(true, "이메일 인증 성공"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "이메일 인증 실패"));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserDTO.RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid UserDTO.LoginRequest request) {
        try {
            String token = userService.login(request);
            return ResponseEntity.ok(new ApiResponse(true, "로그인 성공", new UserDTO.LoginResponse(token)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDTO.UserProfileResponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(new ApiResponse(true, "프로필 조회", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(Authentication authentication,
                                                         @RequestBody @Valid UserDTO.UserProfileUpdateRequest request) {
        try {
            String email = authentication.getName();
            userService.updateUserProfile(email, request);
            return ResponseEntity.ok(new ApiResponse(true, "프로필 수정 성공", email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
