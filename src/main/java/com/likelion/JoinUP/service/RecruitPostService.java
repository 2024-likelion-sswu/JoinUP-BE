package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.RecruitPostDTO;
import com.likelion.JoinUP.dto.UserDTO;
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

    @Transactional
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

    public List<RecruitPostDTO.RecruitPostsResponse> getMyQueuedRecruitPosts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        List<RecruitPost> myPosts = recruitPostRepository.findByWriter(user);
        List<RecruitPost> queuedPosts = recruitPostRepository.findByQueuedUsersContaining(user);

        // 중복 제거
        List<RecruitPost> allPosts = Stream.concat(myPosts.stream(), queuedPosts.stream())
                .distinct()
                .collect(Collectors.toList());

        return allPosts.stream()
                .map(post -> new RecruitPostDTO.RecruitPostsResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getLocation(),
                        post.getMaxMembers(),
                        post.getCurrentMembers(),
                        post.getCreatedAt(),
                        post.getExpiresAt(),
                        post.getWriter().getName()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public void joinQueue(String email, Long recruitPostId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 모집글입니다."));

        // 자신이 작성한 모집글인지 확인
        if (recruitPost.getWriter().equals(user)) {
            throw new RuntimeException("자신이 작성한 모집글에는 줄서기에 참여할 수 없습니다.");
        }

        // 현재 모집 인원이 최대 모집 인원을 초과하지 않도록 체크
        if (recruitPost.getCurrentMembers() >= recruitPost.getMaxMembers()) {
            throw new RuntimeException("모집이 마감되었습니다.");
        }

        // 중복 가입 방지
        if (recruitPost.getQueuedUsers().contains(user)) {
            throw new RuntimeException("이미 줄서기에 참여하셨습니다.");
        }

        recruitPost.getQueuedUsers().add(user);
        recruitPost.setCurrentMembers(recruitPost.getCurrentMembers() + 1);
        recruitPostRepository.save(recruitPost);
    }

    @Transactional
    public void cancelQueue(String email, Long recruitPostId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 모집글입니다."));

        // 사용자가 줄서기에 포함되어 있는지 확인
        if (!recruitPost.getQueuedUsers().contains(user)) {
            throw new RuntimeException("줄서기에 참여하지 않았습니다.");
        }

        recruitPost.getQueuedUsers().remove(user);
        recruitPost.setCurrentMembers(recruitPost.getCurrentMembers() - 1);
        recruitPostRepository.save(recruitPost);
    }

    public RecruitPostDTO.ChatInfoResponse getChatInfo(String chatUserEmail, Long recruitpostId) {
        User chatUser = userRepository.findByEmail(chatUserEmail)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        RecruitPost recruitPost = recruitPostRepository.findById(recruitpostId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 모집글입니다."));
        User chatPartner = recruitPost.getWriter();

        // 자신이 자신의 모집글에 대해 채팅을 요청한 경우 예외 처리
        if (chatUser.equals(chatPartner)) {
            throw new IllegalArgumentException("자신의 모집글에 대해 채팅을 시작할 수 없습니다.");
        }

        return new RecruitPostDTO.ChatInfoResponse(
                new UserDTO.UserProfileResponse(chatUser.getId(), chatUser.getEmail(), chatUser.getName(), chatUser.getProfileImage()),
                new UserDTO.UserProfileResponse(chatPartner.getId(), chatPartner.getEmail(), chatPartner.getName(), chatPartner.getProfileImage())
        );
    }
}
