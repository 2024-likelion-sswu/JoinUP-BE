package com.likelion.JoinUP.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommunityDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        private String content;

        @NotBlank(message = "카테고리는 필수입니다.")
        private String category;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResponse {
        private Long postId;
        private String title;
        private String content;
        private String imageUrl;
        private LocalDateTime createdAt;
        private int likes;
        private String writerName;
        private String category;
        private List<CommentResponse> comments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private Long commentId;
        private String content;
        private LocalDateTime createdAt;
        private String writerName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;
    }
}
