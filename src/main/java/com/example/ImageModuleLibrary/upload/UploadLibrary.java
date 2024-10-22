package com.example.ImageModuleLibrary.upload;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class UploadLibrary {
    private final RestTemplate restTemplate;

    private static final String UPLOAD_URL = "http://localhost:19091/image/upload";

    public UploadLibrary(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile image, Integer size, Integer cachingTime) throws IOException {
        ByteArrayResource resource = getImageByteArrayResource(image);

        // body 설정
        MultiValueMap<String, Object> body = makeRequestBody(size, cachingTime, resource);

        return getResponse(body);
    }

    private String getResponse(MultiValueMap<String, Object> body) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(UPLOAD_URL, requestEntity, String.class);
        return getBody(responseEntity);
    }

    private static MultiValueMap<String, Object> makeRequestBody(Integer size, Integer cachingTime,
                                                                 ByteArrayResource resource) {
        // form-data에 포함할 데이터 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", resource);
        body.add("size", size);
        body.add("cachingTime", cachingTime);
        return body;
    }

    private static String getBody(ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String answer = responseEntity.getBody();

            return getSplitUUID(answer, responseEntity);

        } else {
            throw new RuntimeException("Failed to upload image: " + responseEntity.getStatusCode());
        }
    }

    private static String getSplitUUID(String answer, ResponseEntity<String> responseEntity) {
        // 문자열을 줄 단위로 나누기
        String[] lines = answer.split("\n");

        String dataLine = lines[4].trim();
        if (dataLine.startsWith("data:")) {
            // data: 부분을 ':'로 나누고 두 번째 부분을 반환
            return dataLine.split(":")[2].trim();
        }
        throw new RuntimeException("Data format is wrong: " + responseEntity.getStatusCode());
    }

    private static ByteArrayResource getImageByteArrayResource(MultipartFile image) throws IOException {
        // 파일을 ByteArrayResource로 변환
        byte[] bytes = image.getBytes();

        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename(); // 파일 이름 설정
            }
        };
        return resource;
    }
}
