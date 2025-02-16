package com.laundrygo.shorturl.controller;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.laundrygo.shorturl.service.UrlService;

@RestController
@RequiredArgsConstructor
public class UrlController {

    @Autowired
    private UrlService urlService;

    // private final Map<String, String> urlMap = new HashMap<>();
    // private final Map<String, String> reverseUrlMap = new HashMap<>();
    // private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String URL_SAFE_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz-";
    private static final int SHORT_URL_LENGTH = 5;

    @GetMapping("/short-url")
    public String getShortUrl(@RequestParam("oriUrl") String oriUrl) {

        String existingShortUrl = urlService.getShortUrl(oriUrl);
        if (existingShortUrl != null) {
            return existingShortUrl;
        }

        // 새로운 shortUrl 생성
        String shortUrl;
        int salt = 0;
        do {
            shortUrl = generateShortUrl(oriUrl, salt);
            salt++;
        } while (urlService.getOriginalUrl(shortUrl) != null); // Collision check with DB

        urlService.save(shortUrl, oriUrl);

        return shortUrl;
    }

    @GetMapping("ori-url")
    public String getOriUrl(@RequestParam("shortUrl") String shortUrl) {

        String originalUrl = urlService.getOriginalUrl(shortUrl);

        if (originalUrl != null) {
            return originalUrl;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found");
        }

    }

    private String generateShortUrl(String oriUrl, int salt) {
        try {
            // 32 bytes fixed
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = oriUrl + (salt > 0 ? String.valueOf(salt) : "");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8)); // 32 bytes

            // hash to long
            long hashValue = 0;
            int loopCount = Math.min(hashBytes.length, 8);
            for (int i = 0; i < loopCount; i++) {
                hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
            }

            if (hashValue < 0) {
                hashValue = -hashValue;
            }
            
            // make shortUrl
            StringBuilder shortUrl = new StringBuilder();
            for (int i = 0; i < SHORT_URL_LENGTH; i++) {
                // System.out.println("hashValue" + hashValue);
                long index = hashValue % URL_SAFE_CHARS.length();
                shortUrl.append(URL_SAFE_CHARS.charAt((int) index));
                hashValue /= URL_SAFE_CHARS.length();
            }
            shortUrl.append(".ai");

            return shortUrl.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
