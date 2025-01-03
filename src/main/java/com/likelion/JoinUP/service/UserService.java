package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.UserDTO;
import com.likelion.JoinUP.entity.User;
import com.likelion.JoinUP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }
    public boolean verifyEmail(String email) {
        return email.endsWith("@ac.kr");
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



}