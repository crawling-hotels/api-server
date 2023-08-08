package com.example.demo.image.service;

import com.example.demo.board.exception.URLEncodeFailException;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.image.exception.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class S3ImageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final static String objectFolder = "review";
    private final S3Client s3Client;

    @Transactional
    public String uploadFileV1(MultipartFile multipartFile){
        isValidImageExtension(multipartFile.getOriginalFilename());

        isValidImageExists(multipartFile);

        String fileName = buildFileName(multipartFile.getOriginalFilename());

        try(InputStream inputStream = multipartFile.getInputStream()){
            byte[] fileBytes = inputStream.readAllBytes();

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(multipartFile.getContentType())
                            .build(), RequestBody.fromBytes(fileBytes));

            String fileUrl = s3Client.utilities()
                    .getUrl(builder -> builder
                        .bucket(bucketName)
                        .key(fileName))
                    .toExternalForm();

            return fileUrl;
        }catch (IOException | S3Exception e){
            throw new ImageFileUploadFailedException(e.getMessage());
        }
    }

    @Transactional
    public void deleteFileV1(String imagePath){
        String objectKey = fileNameExtractor(imagePath);

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
        }catch (AwsServiceException | SdkClientException e){
            throw new ImageFileDeleteFailedException(e.getMessage());
        }
    }

    private void isValidImageExtension(String filename) {
        String[] allowedExtensions = { "jpg", "jpeg", "png" };
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if(!Arrays.asList(allowedExtensions).contains(extension)){
            throw new ImageExtensionNotSupportException(ResponseCodeEnum.IMAGE_EXTENSION_NOT_SUPPORT.getMessage());
        }
    }

    private void isValidImageExists(MultipartFile file){
        if (file.isEmpty()) {
            throw new ImageFileEmptyException(ResponseCodeEnum.IMAGE_NOT_EXIST.getMessage());
        }
    }

    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private String buildFileName(String originalFileName){
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return fileName + now + fileExtension;
    }

    private String fileNameExtractor(String imagePath){
        try {
            URL fileUrl = new URL(imagePath);
            String decodedPath = URLDecoder.decode(fileUrl.getPath(), "UTF-8");
            String[] pathSegments = decodedPath.split("/");
            String fileName = pathSegments[pathSegments.length - 1];
            return fileName;
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new ImagePathExtractFailException(ResponseCodeEnum.IMAGE_PATH_EXTRACT_FAILED.getMessage());
        }
    }
}
