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

            String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();

            return fileUrl;
        }catch (IOException | S3Exception e){
            throw new ImageFileUploadFailedException(e.getMessage());
        }
    }

    @Transactional
    public void deleteFileV1(String imagePath){
        s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imagePath)
                        .build());
    }

    private String encodeURL(String input){
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new URLEncodeFailException(ResponseCodeEnum.URL_ENCODE_FAILED.getMessage());
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

    private File convertMultipartFileToFile(MultipartFile multipartFile){
        try {
            File file = new File(multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);

            return file;
        }catch (IOException e) {
            throw new ImageFileConvertException(ResponseCodeEnum.IMAGE_CONVERT_FAILED.getMessage());
        }
    }

    private String extractObjectKeyFromImagePath(String imagePath){
        Pattern pattern = Pattern.compile("/review/(.*)");
        Matcher matcher = pattern.matcher(imagePath);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new ImagePathExtractFailException(ResponseCodeEnum.IMAGE_PATH_EXTRACT_FAILED.getMessage());
    }

    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private String buildFileName(String originalFileName){
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return fileName + now + fileExtension;
    }
}
