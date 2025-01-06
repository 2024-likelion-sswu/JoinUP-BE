package com.likelion.JoinUP.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveImage(MultipartFile file); // 이미지 저장 후 URL 반환
    void deleteImage(String imagePath);
}