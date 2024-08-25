package com.core.book.api.member.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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
    private String bucketName; // S3 버킷

    @Value("${rhkr8521.cdn-domain}")
    private String cdnDomain; // CDN 도메인

    public String uploadFile(String email, MultipartFile file) throws IOException {
        String dir = "profile-images";
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
        // 난수 문자열 생성 (예: 16자 길이)
        String randomString = RandomStringUtils.randomAlphanumeric(16);
        // 파일 경로에 난수 문자열 포함
        String fileKey = dir + "/" + randomString + "/" + fileName;
        // 파일 업로드 시 퍼블릭 읽기 권한을 추가
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .acl("public-read")
                .build();
        // S3 버킷에 이미지 저장
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        // CDN 도메인을 사용한 이미지 URL 반환
        return cdnDomain + "/" + fileKey;
    }
    public void deleteFile(String imageUrl) {
        // 만약 사용자의 image_url이 CDN 도메인 으로 시작한다면 해당 이미지를 버킷에서 삭제
        if (imageUrl != null && imageUrl.startsWith(cdnDomain)) {
            String fileKey = imageUrl.replace(cdnDomain + "/", "");
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}