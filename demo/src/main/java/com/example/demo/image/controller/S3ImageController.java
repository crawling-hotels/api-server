package com.example.demo.image.controller;

import com.example.demo.image.service.S3ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * POSTMAN을 통해 S3에 정상적으로 업로드/조회 되는지 테스트 위한 컨트롤러
 */
@Controller
@RequestMapping("/api/only-test/s3")
public class S3ImageController {
    private final S3ImageService s3ImageService; // S3 이미지 서비스를 주입

    @Autowired
    public S3ImageController(S3ImageService s3ImageService) {
        this.s3ImageService = s3ImageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestPart(value = "file") MultipartFile file) {

        String imageUrl = s3ImageService.uploadFileV1(file);

        return ResponseEntity.ok("Image uploaded successfully. URL: " + imageUrl);
    }

    @DeleteMapping("/{imagePath}")
    public ResponseEntity<String> deleteImage(@PathVariable String imagePath){
        s3ImageService.deleteFileV1(imagePath);

        return ResponseEntity.ok("Image deleted successfully.");
    }

//    @GetMapping("/image/{objectKey}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String objectKey) {
//        try {// S3에서 이미지 조회
//            byte[] imageBytes = s3ImageService.S3PutObjectRequest(objectKey);
//
//            return ResponseEntity.ok()
//                    .header("Content-Type", "image/jpeg") // 이미지 타입에 맞게 변경
//                    .body(imageBytes);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }
}
