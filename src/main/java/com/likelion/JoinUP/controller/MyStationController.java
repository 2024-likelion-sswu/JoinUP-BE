package com.likelion.JoinUP.controller;

import com.likelion.JoinUP.dto.ApiResponse;
import com.likelion.JoinUP.dto.MyStationDTO;
import com.likelion.JoinUP.service.MyStationService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my-stations")
public class MyStationController {

    private final MyStationService myStationService;

    public MyStationController(MyStationService myStationService) {
        this.myStationService = myStationService;
    }

    // 나의 정류장 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getMyStations(Authentication authentication) {
        String email = authentication.getName();
        List<MyStationDTO.MyStationResponse> stations = myStationService.getMyStations(email);
        return ResponseEntity.ok(new ApiResponse(true, "나의 정류장 리스트입니다.", stations));
    }

    // 정류장 추가
    @PostMapping
    public ResponseEntity<ApiResponse> addMyStation(Authentication authentication,
                                                    @RequestBody @Validated MyStationDTO.AddMyStationRequest request) {
        String email = authentication.getName();
        String result = myStationService.addMyStation(email, request);
        if ("중복된 정류장입니다.".equals(result)) {
            return ResponseEntity.ok(new ApiResponse(false, result));
        }
        return ResponseEntity.ok(new ApiResponse(true, result));
    }

    // 정류장 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMyStation(Authentication authentication, @PathVariable Long id) {
        String email = authentication.getName();
        myStationService.deleteMyStation(email, id);
        return ResponseEntity.ok(new ApiResponse(true, "정류장이 삭제되었습니다."));
    }
}
