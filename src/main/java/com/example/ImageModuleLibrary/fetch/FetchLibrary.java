package com.example.ImageModuleLibrary.fetch;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

    public String getResizedCdnUrl(String uuid, Integer size) {
        URI uri = UriComponentsBuilder.fromHttpUrl(FETCH_ORIGINAL_CDN_URL)
                .queryParam("id", uuid)
                .queryParam("size", size)
                .build().toUri();

        return restTemplate.getForObject(uri, String.class);
    }

    public List<String> getMultiOriginalCdnUrl(List<String> uuids) {
        List<String> cdnUrls = new ArrayList<>();
        for (String uuid : uuids) {
            String cdnUrl = getOriginalCdnUrl(uuid);
            cdnUrls.add(cdnUrl);
        }
        return cdnUrls;
    }

    public List<String> getMultiResizedCdnUrl(List<String> uuids, List<Integer> sizes) {
        List<String> cdnUrls = new ArrayList<>();
        for (int i = 0; i < uuids.size(); i++) {
            String cdnUrl = getResizedCdnUrl(uuids.get(i), sizes.get(i));
            cdnUrls.add(cdnUrl);
        }
        return cdnUrls;
    }
}
