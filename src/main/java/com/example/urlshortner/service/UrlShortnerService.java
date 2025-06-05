package com.example.urlshortner.service;

import com.example.urlshortner.model.UrlData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

// @Service: This annotation marks the class as a Spring service component.
// Spring's component scanning will detect this class and register it as a bean in the application context.
// This allows it to be injected into other components (like controllers).
@Service
public class UrlShortnerService {

    // @Autowired: This annotation enables Spring's dependency injection. Spring will automatically
    // create and inject an instance of RedisTemplate<String, Object> into this field.
    // Template already configured in RedisConfig class.
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String URL_PREFIX = "url:";
    private static final String REVERSE_PREFIX = "reverse:";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public UrlData shortenUrl(String originalUrl, String customCode, Long expirationSeconds) {
        String existingCode = (String) redisTemplate.opsForValue().get(REVERSE_PREFIX + originalUrl);
        if (existingCode != null) {
            UrlData existingData = getUrlData(existingCode);
            if (existingData != null && !existingData.isExpiresAt()) {
                return existingData;
            }
        }

        String shortCode;
        if (customCode != null && !customCode.trim().isEmpty()) {
            if (redisTemplate.hasKey(URL_PREFIX + customCode)) {
                throw new RuntimeException("Custom code already exists.");
            }
            shortCode = customCode;
        } else {
            shortCode = generateShortCode();
            while (redisTemplate.hasKey(URL_PREFIX + shortCode)) {
                shortCode = generateShortCode();
            }
        }

        UrlData urlData = new UrlData(originalUrl, shortCode, expirationSeconds);

        redisTemplate.opsForValue().set(URL_PREFIX+shortCode, urlData);
        redisTemplate.opsForValue().set(REVERSE_PREFIX+originalUrl, shortCode);

        if (expirationSeconds != null) {
            redisTemplate.expire(URL_PREFIX + shortCode, expirationSeconds, TimeUnit.SECONDS);
            redisTemplate.expire(REVERSE_PREFIX + originalUrl, expirationSeconds, TimeUnit.SECONDS);
        }

        return urlData;
    }

    public UrlData getOriginalUrl(String shortCode) {
        UrlData urlData = getUrlData(shortCode);
        if (urlData != null && !urlData.isExpiresAt()) {
            urlData.setClickCount(urlData.getClickCount() + 1);
            redisTemplate.opsForValue().set(URL_PREFIX + shortCode, urlData);
            return urlData;
        }
        return null;
    }

    public UrlData getUrlStats(String shortCode) {
        UrlData urlData = getUrlData(shortCode);
        if (urlData != null && !urlData.isExpiresAt()) {
            return urlData;
        }
        return null;
    }

    public boolean deleteUrl(String shortCode) {
        UrlData urlData = getUrlData(shortCode);
        if (urlData != null) {
            redisTemplate.delete(URL_PREFIX + shortCode);
            redisTemplate.delete(REVERSE_PREFIX + urlData.getOriginalUrl());
            return true;
        }
        return false;
    }

    private UrlData getUrlData(String shortCode) {
        Object obj = redisTemplate.opsForValue().get(URL_PREFIX + shortCode);
        if (obj instanceof UrlData) {
            return (UrlData) obj;
        }

        return null;
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
