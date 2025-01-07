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
        return ResponseEntity.ok(new ApiResponse(true, "이메일 인증 성공"));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserDTO.RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공"));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid UserDTO.LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new ApiResponse(true, "로그인 성공", new UserDTO.LoginResponse(token)));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDTO.UserProfileResponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(new ApiResponse(true, "유저 프로필입니다.", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(Authentication authentication,
                                                         @ModelAttribute @Valid UserDTO.UserProfileUpdateRequest request) {
        String email = authentication.getName();
        userService.updateUserProfile(email, request);
        return ResponseEntity.ok(new ApiResponse(true, "프로필이 수정되었습니다."));
    }
}
