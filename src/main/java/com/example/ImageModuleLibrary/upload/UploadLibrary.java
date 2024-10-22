package com.example.ImageModuleLibrary.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String MULTI_UPLOAD_URL = "http://localhost:19091/image/upload/multi";

    public UploadLibrary(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile image, Integer size, Integer cachingTime) throws IOException {
        // body 설정
        MultiValueMap<String, Object> body = makeRequestBody(size, cachingTime, image);
        ResponseEntity<String> responseEntity = sendRequest(UPLOAD_URL, body);
        return getBody(responseEntity);
    }

    public List<String> uploadMultiImage(MultipartFile[] files, Integer[] sizes, Integer[] cachingTimes)
            throws IOException {
        MultiValueMap<String, Object> body = makeMultiRequestBody(files, sizes, cachingTimes);
        ResponseEntity<String> responseEntity = sendRequest(MULTI_UPLOAD_URL, body);
        return getMultiBody(responseEntity);
    }

    private static MultiValueMap<String, Object> makeMultiRequestBody(MultipartFile[] files, Integer[] sizes,
                                                                      Integer[] cachingTimes)
            throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for (int i = 0; i < files.length; i++) {
            ByteArrayResource resource = getImageByteArrayResource(files[i]);
            body.add("files", resource);
            body.add("sizes", sizes[i]);
            body.add("cachingTimes", cachingTimes[i]);
        }
        return body;
    }

    private ResponseEntity<String> sendRequest(String url, MultiValueMap<String, Object> body) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(url, requestEntity, String.class);
    }

    private static MultiValueMap<String, Object> makeRequestBody(Integer size, Integer cachingTime,
                                                                 MultipartFile image) throws IOException {
        // form-data에 포함할 데이터 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource resource = getImageByteArrayResource(image);
        body.add("file", resource);
        body.add("size", size);
        body.add("cachingTime", cachingTime);
        return body;
    }

    private static List<String> getMultiBody(ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {

            return getMultiSplitUUID(responseEntity);

        } else {
            throw new RuntimeException("Failed to upload image: " + responseEntity.getStatusCode());
        }
    }

    private static List<String> getMultiSplitUUID(ResponseEntity<String> responseEntity) {
        String answer = responseEntity.getBody();

        String[] lines = answer.split("\n");

        // 결과 리스트
        List<String> resultList = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("event:SUCCESS")) {
                String line = lines[i + 1];
                String[] parts = line.split(":");
                resultList.add(parts[2]);
            }
        }
        return resultList;
    }

    private static String getBody(ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return getSplitUUID(responseEntity);

        } else {
            throw new RuntimeException("Failed to upload image: " + responseEntity.getStatusCode());
        }
    }

    private static String getSplitUUID(ResponseEntity<String> responseEntity) {
        String answer = responseEntity.getBody();
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

        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename(); // 파일 이름 설정
            }
        };
    }
}
