# 📄 Image Module Library

<br>

## ❓ How to use Image Module Library in Spring Project

### 1. build.gradle 수정

<br>

build.gradle의 repositories, dependencies에 아래 것 추가

추가 후 build.gradle refresh해서 라이브러리 가져와야 함

<br>

``` java
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation('com.github.CJ-1998:Library-test:test7') {
        artifact {
            name = 'Library-test'
            classifier = 'plain'
        }
    }
}
```

<br>

### 2. 프로젝트의 configuration 클래스 생성

<br> 

Spring Project의 원하는 위치에 아래처럼 configuration 클래스 생성

클래스 이름은 상관 없음

아래 예시에서 클래스 이름은 config

<br>

``` java
import com.example.ImageModuleLibrary.fetch.FetchLibrary;
import com.example.ImageModuleLibrary.upload.UploadLibrary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class config {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FetchLibrary fetchLibrary(RestTemplate restTemplate) {
        return new FetchLibrary(restTemplate);
    }

    @Bean
    public UploadLibrary uploadLibrary(RestTemplate restTemplate) {
        return new UploadLibrary(restTemplate);
    }
}
```

<br>

### 3. 이미지 모듈 라이브러리 사용할 곳에서 주입 받아 메서드 사용

<br>

**예시**

```java 
@Service 
public class TestService{

    private final UploadLibrary uploadLibrary;
    private final FetchLibrary fetchLibrary;

    @Autowired
    public TestService(UploadLibrary uploadLibrary, FetchLibrary fetchLibrary){
        this.uploadLibrary = uploadLibrary;
        this.fetchLibrary = fetchLibrary;
    }

    // uploadLibrary, fetchLibrary의 메서드 사용

}
```

<br>

사용 가능 메서드는 아래와 같음

<br>

### 📤 **UploadLibrary**

이미지 업로드를 쉽게 할 수 있는 메서드가 있는 곳

<br>

### 1️⃣ uploadImage

- `메서드 명`
  - uploadImage

- `메서드 설명`
  - 이미지 하나 업로드할 수 있는 메서드 

- `parameter`
  - MultipartFile image 
    - 업로드 할 이미지
  - Integer size 
    - resizing한 이미지의 가로, 세로 중 긴 쪽의 픽셀 수
  - Integer cachingTime
    - 이미지 CDN에 caching 될 시간 (분 단위)

- `return type`
  - String 
    - 이미지를 가리키는 UUID
    - 추후 이미지의 CDN URL 얻을 때 필요해 저장 필요
    
- `예시`
    ``` java
    MultipartFile image = (프론트로부터 받은 MultipartFile);
    Integer size = 300;
    Integer cachingTime = 1;
    String uuid = uploadLibrary.uploadImage(image, size, cachingTime);
    ```

<br>

### 2️⃣ uploadMultiImage

- `메서드 명`
  - uploadMultiImage

- `메서드 설명`
  - 이미지 여러 개 업로드 할 수 있는 메서드 

- `parameter` 
  - MultipartFile[ ] images 
    - 업로드 할 이미지들
  - Integer[ ] sizes 
    - 각 이미지의 resizing한 이미지의 가로, 세로 중 긴 쪽의 픽셀 수
  - Integer[ ] cachingTimes
    - 각 이미지 CDN에 caching 될 시간 (분 단위)

- `return type`
  - List< String >
    - 각 이미지를 가리키는 UUID들의 List
    - 추후 이미지의 CDN URL 얻을 때 필요해 저장 필요

- `주의 사항`
  - 각 parameter의 배열 길이가 동일해야 함

- `예시`
    ```java
    MultipartFile[] images = (프론트로부터 받은 MultipartFile들);
    Integer[] sizes = new Integer[]{300,500};
    Integer[] cachingTimes = new Integer[]{1, 2};
    List<String> uuids = uploadLibrary.uploadMultiImage(images, sizes, cachingTimes);
    ```

<br>

---

<br>

### 📥 **FetchLibrary**

업로드한 이미지를 조회, 다운로드 할 수 있는 CDN URL 조회할 수 있는 메서드 있는 곳

CDN URL을 프론트엔드로 반환해 img 태그에 넣으면 됨

<br>

### 1️⃣ getOriginalCdnUrl

- `메서드 명`
  - getOriginalCdnUrl

- `메서드 설명`
  - 원본 이미지의 CDN URL 조회할 수 있는 메서드 

- `parameter`
  - String uuid
    - 이미지 업로드 후 받은 uuid 값

- `return type`
  - String 
    - 이미지 조회할 수 있는 CDN URL
    
- `예시`
    ```java
    String uuid = "bb9fb408-5b14-499d-a3ce-1bafe6aae208";
    String cdnUrl = fetchLibrary.getOriginalCdnUrl(uuid);
    ```

<br>

### 2️⃣ getResizedCdnUrl

- `메서드 명`
  - getResizedCdnUrl

- `메서드 설명`
  - resizing 이미지의 CDN URL 조회할 수 있는 메서드 

- `parameter`
  - String uuid
    - 이미지 업로드 후 받은 uuid 값
  - Integer size
    - 이미지 업로드 시 요청한 size 값

- `return type`
  - String 
    - 이미지 조회할 수 있는 CDN URL
    
- `예시`
    ```java
    String uuid = "bb9fb408-5b14-499d-a3ce-1bafe6aae208";
    Integer size = 300;
    String cdnUrl = fetchLibrary.getResizedCdnUrl(uuid, size);
    ```
<br>

### 3️⃣ getMultiOriginalCdnUrl

- `메서드 명`
  - getMultiOriginalCdnUrl

- `메서드 설명`
  - 원본 이미지 여러 개의 CDN URL 조회할 수 있는 메서드 

- `parameter`
  - List< String > uuids
    - 각 이미지 업로드 후 받은 uuid 값들의 List

- `return type`
  - List< String >
    - 각 이미지 조회할 수 있는 CDN URL들의 List
    
- `예시`
    ```java
    List<String> uuids = (UUID의 List);
    List<String> cdnUrls = fetchLibrary.getMultiOriginalCdnUrl(uuids);
    ```

<br>

### 4️⃣ getMultiResizedCdnUrl

- `메서드 명`
  - getMultiResizedCdnUrl

- `메서드 설명`
  - resizing 이미지 여러 개의 CDN URL 조회할 수 있는 메서드 

- `parameter`
  - List< String > uuids
    - 각 이미지 업로드 후 받은 uuid 값들의 List
  - List< Integer > sizes
    - 각 이미지 업로드 시 요청한 size 값들의 List

- `return type`
  - List< String >
    - 각 이미지 조회할 수 있는 CDN URL들의 List

- `주의 사항`
  - 각 parameter의 배열 길이가 동일해야 함    

- `예시`
    ```java
    List<String> uuids = (UUID의 List);
    List<Integer> sizes = (size의 List);
    List<String> cdnUrls = fetchLibrary.getMultiResizedCdnUrl(uuids, sizes);
    ```

<br>

---
