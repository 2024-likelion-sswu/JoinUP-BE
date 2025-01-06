package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.CommunityDTO;
import com.likelion.JoinUP.entity.CommunityPost;
import com.likelion.JoinUP.entity.User;
import com.likelion.JoinUP.repository.CommunityRepository;
import com.likelion.JoinUP.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    private final CommunityRepository postRepository;
    private final UserRepository userRepository;

    public CommunityService(CommunityRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 게시글 작성
    public void createPost(String email, CommunityDTO.CreatePostRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        CommunityPost.Category category = CommunityPost.Category.valueOf(request.getCategory().toUpperCase());

        CommunityPost post = CommunityPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .createdAt(LocalDateTime.now())
                .likes(0)
                .category(category)
                .writer(user)
                .build();

        postRepository.save(post);
    }

    // 게시글 조회
    public List<CommunityDTO.PostResponse> getPostsByCategory(String category) {
        CommunityPost.Category postCategory = CommunityPost.Category.valueOf(category.toUpperCase());

        List<CommunityPost> posts = postCategory == CommunityPost.Category.HOT
                ? postRepository.findByLikesGreaterThanEqual(10)
                : postRepository.findByCategory(postCategory);

        return posts.stream().map(post -> new CommunityDTO.PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getLikes(),
                post.getWriter().getName(),
                post.getCategory().name(),
                post.getComments().stream().map(comment -> new CommunityDTO.CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getWriter().getName()
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }



}
