package com.likelion.JoinUP.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Profile({"local", "test"})
public class LocalStorageService implements StorageService {

    @Override
    public String saveImage(MultipartFile file) {
        try {
            System.out.println("Saving file: " + file.getOriginalFilename());

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                throw new RuntimeException("파일 이름이 유효하지 않습니다.");
            }
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID() + "_" + sanitizedFileName;
            Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads/" + fileName);

            // 디렉토리 생성
            File dir = new File(filePath.getParent().toString());
            if (!dir.exists()) {
                System.out.println("Creating directory: " + dir.getAbsolutePath());
                dir.mkdirs();
            }

            System.out.println("Saving to path: " + filePath.toString());
//            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());

            // MIME 타입 검증
            String mimeType = Files.probeContentType(filePath);
            if (!List.of("image/png", "image/jpeg").contains(mimeType)) {
                Files.delete(filePath); // 잘못된 파일 삭제
                throw new RuntimeException("허용되지 않은 파일 형식입니다.");
            }
            System.out.println("File saved successfully");
            return "/uploads/" + fileName;
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }


    @Override
    public void deleteImage(String imagePath) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir") + imagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("이미지 삭제 실패", e);
        }
    }

}
