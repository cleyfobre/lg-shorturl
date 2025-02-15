package com.laundrygo.shorturl.controller;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
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
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_URL_LENGTH = 8;

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
            // 16 bytes fixed
            MessageDigest md = MessageDigest.getInstance("MD5");
            String input = oriUrl + (salt > 0 ? String.valueOf(salt) : "");
            byte[] hashBytes = md.digest(input.getBytes());

            // hash to BigInteger
            BigInteger hashValue = new BigInteger(1, hashBytes);

            // make shortUrl
            StringBuilder shortUrl = new StringBuilder();
            for (int i = 0; i < SHORT_URL_LENGTH; i++) {
                BigInteger index = hashValue.mod(BigInteger.valueOf(BASE62.length()));
                shortUrl.append(BASE62.charAt(index.intValue()));
                hashValue = hashValue.divide(BigInteger.valueOf(BASE62.length()));
            }

            // check collision
            if (urlMap.containsKey(shortUrl.toString())) {
                return generateShortUrl(oriUrl, salt + 1);
            }

            return shortUrl.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
