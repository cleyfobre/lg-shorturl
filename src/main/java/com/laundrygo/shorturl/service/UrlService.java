package com.laundrygo.shorturl.service;

import org.springframework.stereotype.Service;

import com.laundrygo.shorturl.domain.Url;
import com.laundrygo.shorturl.repository.UrlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

    public String getOriginalUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        return url != null ? url.getOriginalUrl() : null;
    }

    public String getShortUrl(String originalUrl) {
        Url url = urlRepository.findByOriginalUrl(originalUrl);
        return url != null ? url.getShortUrl() : null;
    }

    public void save(String shortUrl, String originalUrl) {
        Url url = new Url();
        url.setShortUrl(shortUrl);
        url.setOriginalUrl(originalUrl);
        urlRepository.save(url);
    }
}
