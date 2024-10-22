package com.example.ImageModuleLibrary.fetch;

import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class FetchLibrary {
    private final RestTemplate restTemplate;

    private static final String FETCH_ORIGINAL_CDN_URL = "http://localhost:19091/fetch/cdnUrl";

    public FetchLibrary(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getOriginalCdnUrl(String uuid) {
        URI uri = UriComponentsBuilder.fromHttpUrl(FETCH_ORIGINAL_CDN_URL)
                .queryParam("id", uuid).build().toUri();

        return restTemplate.getForObject(uri, String.class);
    }
}
