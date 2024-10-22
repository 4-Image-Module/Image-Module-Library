package com.example.ImageModuleLibrary.Test;

import com.example.ImageModuleLibrary.fetch.FetchLibrary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test/fetch")
public class FetchLibraryTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final FetchLibrary fetchLibrary = new FetchLibrary(restTemplate);

    @GetMapping
    public String getOriginalCdnUrlTest(@RequestParam("id") String id) {
        return fetchLibrary.getOriginalCdnUrl(id);
    }

    @GetMapping("/multi")
    public String getResizedCdnUrlTest(@RequestParam("id") String id, @RequestParam("size") Integer size) {
        return fetchLibrary.getResizedCdnUrl(id, size);
    }

}
