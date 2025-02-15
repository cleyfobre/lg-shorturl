package com.laundrygo.shorturl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    @GetMapping("/short-url")
    public String getShortUrl(@RequestParam("oriUrl") String oriUrl) {
        return "short url";
    }

    @GetMapping("ori-url")
    public String getOriUrl(@RequestParam("shortUrl") String shortUrl) {
        return "ori url";
    }

}
