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

    private UserService userService;
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


}
