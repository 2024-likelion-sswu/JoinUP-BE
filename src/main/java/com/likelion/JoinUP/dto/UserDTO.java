package com.likelion.JoinUP.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


public class UserDTO {
    //회원인증
    @Data
    public static class VerificationRequest {
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@ac\\.kr$", message = "학교 이메일이 아닙니다.")
        private String email;
    }

    //회원가입
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        private String email;

        @NotBlank(message = "Password is required.")
        private String password;

        @NotBlank(message = "Name is required.")
        private String name;

        @Pattern(regexp = "^(\\d{4})(\\d{2})(\\d{2})$", message = "생년월일로 입력해주세요.")
        private String dateOfBirth;
    }

    //로그인
    @Data
    public static class LoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResponse {
        private String token;
    }

    //프로필수정
    @Data
    public static class UserProfileUpdateRequest {
        private String name;
        private String dateOfBirth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private String email;
        private String name;
        private String dateOfBirth;
    }
}
