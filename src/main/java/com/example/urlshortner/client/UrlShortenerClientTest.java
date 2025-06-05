package com.example.urlshortner.client;

import com.example.urlshortner.grpc.GetOriginalUrlResponse;
import com.example.urlshortner.grpc.GetUrlStatsResponse;
import com.example.urlshortner.grpc.ShortenUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
// CommandLineRunner is a Spring Boot interface. Any class that implements this interface and is a Spring component
// will have its run() method executed automatically once the Spring application context has fully loaded and right
// before the application starts taking external requests.
public class UrlShortenerClientTest implements CommandLineRunner {

    @Autowired
    private UrlShortnerClient client;

    @Override
    public void run(String... args) throws Exception {
        // Example usage - remove or comment out in production
        testUrlShortener();
    }

    private void testUrlShortener() {
        try {
            // Test shortening URL
            ShortenUrlResponse shortenResponse = client.shortenUrl(
                    "https://www.google.com",
                    null,
                    3600 // 1 hour expiration
            );
            System.out.println("Shorten Response: " + shortenResponse);

            if (shortenResponse.getSuccess()) {
                String shortCode = shortenResponse.getShortCode();

                // Test getting original URL
                System.out.println("Getting Original URL for: " + shortCode);
                GetOriginalUrlResponse originalResponse = client.getOriginalUrl(shortCode);
                System.out.println("Original URL Response: " + originalResponse);

                // Test getting stats
                System.out.println("Getting Stats for: " + shortCode);
                GetUrlStatsResponse statsResponse = client.getUrlStats(shortCode);
                System.out.println("Stats Response: " + statsResponse);
            }

        } catch (Exception e) {
            System.err.println("Error testing URL shortener: " + e.getMessage());
        }
    }
}