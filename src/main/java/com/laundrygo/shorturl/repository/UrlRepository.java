package com.laundrygo.shorturl.repository;

import org.apache.ibatis.annotations.Mapper;

import com.laundrygo.shorturl.domain.Url;

@Mapper
public interface UrlRepository {
    Url findByShortUrl(String shortUrl);
    Url findByOriginalUrl(String originalUrl);
    void save(Url url);
}
