package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.UserDTO;
import com.likelion.JoinUP.entity.User;
import com.likelion.JoinUP.repository.UserRepository;
import com.likelion.JoinUP.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageService storageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                       StorageService storageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.storageService = storageService;
    }

    public void register(UserDTO.RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("중복된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        userRepository.save(user);
    }

    public String login(UserDTO.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("로그인 정보가 일치하지 않습니다.");
        }
        return jwtTokenProvider.createToken(request.getEmail());
    }

    public UserDTO.UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
        return new UserDTO.UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImage()
        );
    }

    public void updateUserProfile(String email, UserDTO.UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            if (user.getProfileImage() != null) {
                try {
                    storageService.deleteImage(user.getProfileImage());
                } catch (Exception e) {
                    System.err.println("기존 이미지 삭제 중 오류 발생: " + e.getMessage());
                    // 로그만 남기고 계속 진행
                }
            }
            String imageUrl = storageService.saveImage(request.getProfileImage());
            user.setProfileImage(imageUrl);
        }

        userRepository.save(user);
    }

}