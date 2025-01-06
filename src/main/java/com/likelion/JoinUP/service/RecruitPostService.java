package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.RecruitPostDTO;
import com.likelion.JoinUP.entity.RecruitPost;
import com.likelion.JoinUP.entity.User;
import com.likelion.JoinUP.repository.RecruitPostRepository;
import com.likelion.JoinUP.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecruitPostService {
    private final RecruitPostRepository recruitPostRepository;
    private final UserRepository userRepository;

    public RecruitPostService(RecruitPostRepository recruitPostRepository, UserRepository userRepository) {
        this.recruitPostRepository = recruitPostRepository;
        this.userRepository = userRepository;
    }

    public void createRecruitPost(String email, RecruitPostDTO.CreateRecruitPostRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        RecruitPost recruitPost = RecruitPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .maxMembers(request.getMaxMembers())
                .currentMembers(1)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(request.getPeriod()))
                .writer(user)
                .build();

        recruitPostRepository.save(recruitPost);
    }

    public List<RecruitPostDTO.RecruitPostsResponse> getRecruitPostsByLocation(String location) {
        List<RecruitPost> recruitPosts = recruitPostRepository.findByLocationContaining(location);
//        List<RecruitPost> recruitPosts = recruitPostRepository.findValidPostsByLocation(location);
        return recruitPosts.stream()
                .filter(post -> post.getCurrentMembers() < post.getMaxMembers()) // 모집 정원이 차지 않은 글
                .filter(post -> post.getExpiresAt().isAfter(LocalDateTime.now())) // 만료일시가 현재보다 이후인 글
                .map(recruitPost -> new RecruitPostDTO.RecruitPostsResponse(
                recruitPost.getId(),
                recruitPost.getTitle(),
                recruitPost.getContent(),
                recruitPost.getLocation(),
                recruitPost.getMaxMembers(),
                recruitPost.getCurrentMembers(),
                recruitPost.getCreatedAt(),
                recruitPost.getExpiresAt(),
                recruitPost.getWriter().getName()
        )).collect(Collectors.toList());
    }




}
