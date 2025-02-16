package com.laundrygo.shorturl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Url {
    private String shortUrl;
    private String originalUrl;
}
