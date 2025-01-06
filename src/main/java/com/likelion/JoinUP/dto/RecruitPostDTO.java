package com.likelion.JoinUP.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RecruitPostDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRecruitPostRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Size(max = 200, message = "본문은 최대 200자까지 입력 가능합니다.")
        private String content;

        @NotBlank(message = "위치는 필수입니다.")
        private String location;

        @NotNull
        @Min(value = 2, message = "최소 1명 이상 모집해야 합니다.")
        @Max(value = 6, message = "최대 6명까지만 모집할 수 있습니다.")
        private int maxMembers;

        @NotNull
        @Min(value = 5, message = "모집 시간은 최소 5분이어야 합니다.")
        @Max(value = 60, message = "모집 시간은 최대 60분까지 가능합니다.")
        private int period;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecruitPostsResponse {
        private Long recruitPostId;
        private String title;
        private String content;
        private String location;
        private int maxMembers;
        private int currentMembers;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private String writerName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatInfoResponse {
        private UserDTO.UserProfileResponse chatUser;
        private UserDTO.UserProfileResponse chatPartner;
    }
}
