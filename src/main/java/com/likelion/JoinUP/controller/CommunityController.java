package com.likelion.JoinUP.controller;

import com.likelion.JoinUP.dto.ApiResponse;
import com.likelion.JoinUP.dto.CommunityDTO;
import com.likelion.JoinUP.service.CommunityService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse> createPost(Authentication authentication,
                                                  @RequestBody @Validated CommunityDTO.CreatePostRequest request) {
        String email = authentication.getName();
        communityService.createPost(email, request);
        return ResponseEntity.ok(new ApiResponse(true, "게시글이 등록되었습니다."));
    }

    // 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse> getPostsByCategory(@RequestParam("category") String category) {
        List<CommunityDTO.PostResponse> posts = communityService.getPostsByCategory(category);
        return ResponseEntity.ok(new ApiResponse(true, "게시글 조회 성공", posts));
    }

    // 댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse> addComment(Authentication authentication,
                                                  @PathVariable Long postId,
                                                  @RequestBody @Validated CommunityDTO.CreateCommentRequest request) {
        String email = authentication.getName();
        communityService.addComment(email, postId, request);
        return ResponseEntity.ok(new ApiResponse(true, "댓글이 등록되었습니다."));
    }

    // 게시글 좋아요
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse> likePost(Authentication authentication, @PathVariable Long postId) {
        try {
            String email = authentication.getName();
            communityService.likePost(email, postId);
            return ResponseEntity.ok(new ApiResponse(true, "좋아요가 저장되었습니다."));
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
