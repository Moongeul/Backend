package com.core.book.api.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String email, MultipartFile file) throws IOException {
        String dir = "profile-images";
        String defaultUrl = "https://moongeul.s3.ap-northeast-2.amazonaws.com";

        // 현재 날짜와 시간 가져오기
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());

        // 파일 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 이메일 + 현재날짜 + 현재시간 + 확장자 형식으로 파일 이름 설정
        String fileName = email + "_" + currentDateTime + extension;
        String fileKey = dir + "/" + fileName;

        // 파일 업로드 시 퍼블릭 읽기 권한을 추가
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .acl("public-read")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return defaultUrl + "/" + fileKey;
    }

    public void deleteFile(String imageUrl) {
        // 만약 사용자의 image_url이 "https://moongeul.s3" 로 시작한다면 해당 이미지를 버킷에서 삭제
        if (imageUrl != null && imageUrl.startsWith("https://moongeul.s3")) {
            String fileKey = imageUrl.replace("https://moongeul.s3.ap-northeast-2.amazonaws.com/", "");

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
