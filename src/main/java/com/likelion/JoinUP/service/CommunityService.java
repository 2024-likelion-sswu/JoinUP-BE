package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.CommunityDTO;
import com.likelion.JoinUP.entity.CommunityPost;
import com.likelion.JoinUP.entity.Comment;
import com.likelion.JoinUP.entity.CommunityPostLike;
import com.likelion.JoinUP.entity.User;
import com.likelion.JoinUP.repository.CommentRepository;
import com.likelion.JoinUP.repository.CommunityRepository;
import com.likelion.JoinUP.repository.LikeRepository;
import com.likelion.JoinUP.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    private final CommunityRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public CommunityService(CommunityRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository, UserRepository userRepository, StorageService storageService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    // 게시글 작성
    @Transactional
    public void createPost(String email, CommunityDTO.CreatePostRequest request, MultipartFile image) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 이미지 업로드
        String imageUrl = null;
        if (image != null) {
            imageUrl = storageService.saveImage(image);
        }

        // 게시글 저장
        CommunityPost post = CommunityPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(CommunityPost.Category.valueOf(request.getCategory().toUpperCase()))
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .writer(user)
                .build();

        postRepository.save(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(String email, Long postId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        // 작성자 권한 확인
        if (!post.getWriter().equals(user)) {
            throw new RuntimeException("게시글 삭제 권한이 없습니다.");
        }

        // 이미지 삭제
        if (post.getImageUrl() != null) {
            storageService.deleteImage(post.getImageUrl());
        }

        // 게시글 삭제
        postRepository.delete(post);
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

    // 댓글 작성
    @Transactional
    public void addComment(String email, Long postId, CommunityDTO.CreateCommentRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .writer(user)
                .post(post)
                .build();

        commentRepository.save(comment);
    }

    // 좋아요 추가
    @Transactional
    public void likePost(String email, Long postId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        // 좋아요 중복 처리
        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new RuntimeException("이미 좋아요를 누르셨습니다.");
        }

        // 좋아요 추가
        CommunityPostLike communityPostLike = CommunityPostLike.builder()
                .post(post)
                .user(user)
                .build();
        likeRepository.save(communityPostLike);

        // 게시글의 좋아요 수 증가
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

}
