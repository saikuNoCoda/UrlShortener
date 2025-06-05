package com.example.urlshortner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

// ignoreUnknown = true: This tells Jackson that if it encounters any properties in the JSON input that are
// not defined as fields in this UrlData class, it should ignore them silently instead of throwing an error.
// This makes the deserialization process more robust to changes in the JSON structure
// (e.g., if a new field is added to the JSON that this version of the class doesn't know about).
@JsonIgnoreProperties(ignoreUnknown = true)
//This interface is a marker interface from Java's core library. Implementing Serializable indicates
// that objects of this class can be converted into a byte stream for sending over network.
public class UrlData implements Serializable {
    // @JsonProperty annotation (also from Jackson) is used to map the Java field names to JSON property
    // names during serialization and deserialization.
    @JsonProperty("originalUrl")
    private String originalUrl;

    @JsonProperty("shortCode")
    private String shortCode;

    @JsonProperty("clickCount")
    private Long clickCount;

    @JsonProperty("createdAt")
    private Long createdAt;

    @JsonProperty("expiresAt")
    private Long expiresAt;

    public UrlData() {
    }

    public UrlData(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = 0L;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = null;
    }

    public UrlData(String originalUrl, String shortCode, Long expirationSeconds) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = 0L;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = expirationSeconds != null ? System.currentTimeMillis() + (expirationSeconds * 1000) : null;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpiresAt() {
        return expiresAt != null && System.currentTimeMillis() > expiresAt;
    }
}
