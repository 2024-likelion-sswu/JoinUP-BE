package com.likelion.JoinUP.controller;

import com.likelion.JoinUP.dto.ApiResponse;
import com.likelion.JoinUP.dto.RecruitPostDTO;
import com.likelion.JoinUP.service.RecruitPostService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruit-posts")
public class RecruitPostController {

    private final RecruitPostService recruitPostService;

    public RecruitPostController(RecruitPostService recruitPostService) {
        this.recruitPostService = recruitPostService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRecruitPost(Authentication authentication,
                                                         @RequestBody @Validated RecruitPostDTO.CreateRecruitPostRequest request) {
        String email = authentication.getName(); // 인증된 사용자의 이메일 추출
        recruitPostService.createRecruitPost(email, request);
        return ResponseEntity.ok(new ApiResponse(true, "모집글이 등록되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getRecruitPostsByLocation(@RequestParam("location") String location) {
        List<RecruitPostDTO.RecruitPostsResponse> posts = recruitPostService.getRecruitPostsByLocation(location);
        if (posts.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(true, "요청하신 위치에 대한 모집글이 없습니다."));
        }
        return ResponseEntity.ok(new ApiResponse(true, "요청하신 위치에 대한 모집글입니다.", posts));
    }

    @PostMapping("/{recruit_id}/join")
    public ResponseEntity<ApiResponse> joinQueue(Authentication authentication, @PathVariable Long recruit_id) {
        String email = authentication.getName();
        recruitPostService.joinQueue(email, recruit_id);
        return ResponseEntity.ok(new ApiResponse(true, "줄서기에 추가되었습니다."));
    }

    @DeleteMapping("/{recruit_id}/join")
    public ResponseEntity<ApiResponse> cancelQueue(Authentication authentication, @PathVariable Long recruit_id) {
        String email = authentication.getName();
        recruitPostService.cancelQueue(email, recruit_id);
        return ResponseEntity.ok(new ApiResponse(true, "줄서기가 취소되었습니다."));
    }

    @GetMapping("/myjoin")
    public ResponseEntity<ApiResponse> getMyQueuedRecruitPosts(Authentication authentication) {
        String email = authentication.getName();
        List<RecruitPostDTO.RecruitPostsResponse> posts = recruitPostService.getMyQueuedRecruitPosts(email);
        if (posts.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(true, "줄서기에 참여한 모집글이 없습니다."));
        }
        return ResponseEntity.ok(new ApiResponse(true, "나의 줄서기입니다.", posts));
    }

    @GetMapping("/{recruit_Id}/chat")
    public ResponseEntity<ApiResponse> getChatInfo(Authentication authentication,
                                                   @PathVariable Long recruit_Id) {
        String currentUserEmail = authentication.getName();
        RecruitPostDTO.ChatInfoResponse chatInfo = recruitPostService.getChatInfo(currentUserEmail, recruit_Id);
        return ResponseEntity.ok(new ApiResponse(true, "채팅방 정보를 가져왔습니다.", chatInfo));
    }
}
