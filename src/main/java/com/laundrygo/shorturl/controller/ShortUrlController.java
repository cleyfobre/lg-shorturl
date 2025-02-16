package com.laundrygo.shorturl.controller;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final Map<String, String> urlMap = new HashMap<>();
    private final Map<String, String> reverseUrlMap = new HashMap<>();
    // private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String URL_SAFE_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz-";
    private static final int SHORT_URL_LENGTH = 5;

    @GetMapping("/short-url")
    public String getShortUrl(@RequestParam("oriUrl") String oriUrl) {
        if (reverseUrlMap.containsKey(oriUrl)) {
            return reverseUrlMap.get(oriUrl);
        }

        // 새로운 shortUrl 생성
        String shortUrl = generateShortUrl(oriUrl, 0); // Initial attempt with salt = 0
        urlMap.put(shortUrl, oriUrl);
        reverseUrlMap.put(oriUrl, shortUrl);

        return shortUrl;
    }

    @GetMapping("ori-url")
    public String getOriUrl(
            @RequestParam("shortUrl") String shortUrl) {

        if (urlMap.containsKey(shortUrl)) {
            return urlMap.get(shortUrl);
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

            // check collision
            if (urlMap.containsKey(shortUrl.toString())) {
                return generateShortUrl(oriUrl, salt + 1);
            }

            return shortUrl.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
