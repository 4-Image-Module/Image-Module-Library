package com.example.ImageModuleLibrary.Test;

import com.example.ImageModuleLibrary.upload.UploadLibrary;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test/upload")
public class UploadLibraryTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UploadLibrary uploadLibrary = new UploadLibrary(restTemplate);

    @PostMapping
    public String uploadTest(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadLibrary.uploadImage(file, 100, 1);
    }

    @PostMapping("/multi")
    public List<String> uploadTestMulti(@RequestParam("files") MultipartFile[] files) throws IOException {
        Integer[] sizes = new Integer[]{100, 200};
        Integer[] cachingTimes = new Integer[]{1, 2};
        return uploadLibrary.uploadMultiImage(files, sizes, cachingTimes);
    }
}
