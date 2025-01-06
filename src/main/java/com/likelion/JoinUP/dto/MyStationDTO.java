package com.likelion.JoinUP.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MyStationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddMyStationRequest {
        @NotBlank(message = "정류장 이름은 필수입니다.")
        private String stationName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyStationResponse {
        private Long id;
        private String stationName;
    }
}
