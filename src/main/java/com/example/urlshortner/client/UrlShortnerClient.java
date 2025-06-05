package com.example.urlshortner.client;

import com.example.urlshortner.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

// @Component annotation marks UrlShortnerClient as a Spring component.
// This means Spring's component scanning will detect this class and create a singleton instance of it,
// making it eligible for dependency injection into other parts of your application.
@Component
public class UrlShortnerClient {

    //  ManagedChannel represents a long-lived connection to a gRPC server. It handles the underlying network
    //  communication, including connection management, load balancing, and more
    private final ManagedChannel channel;

    // A gRPC stub is a client-side representation of the remote service. It provides methods that correspond to the
    // RPC (Remote Procedure Call) methods defined in url_shortner.proto file
    // Blocking (synchronous) RPC calls. This means the client thread will wait until the server responds
    // to the RPC call before proceeding.
    private final UrlShortenerServiceGrpc.UrlShortenerServiceBlockingStub stub;

    public UrlShortnerClient() {
        // This creates a ManagedChannelBuilder instance configured to connect to a gRPC server
        // running on localhost at port 9090.
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext().build();
        this.stub = UrlShortenerServiceGrpc.newBlockingStub(channel);
    }
    public ShortenUrlResponse shortenUrl(String originalUrl, String customCode, long expirationSeconds) {
        ShortenUrlRequest request = ShortenUrlRequest.newBuilder()
                .setOriginalUrl(originalUrl)
                .setCustomCode(customCode != null ? customCode : "")
                .setExpirationSeconds(expirationSeconds)
                .build();

        return stub.shortenUrl(request);
    }

    public GetOriginalUrlResponse getOriginalUrl(String shortCode) {
        GetOriginalUrlRequest request = GetOriginalUrlRequest.newBuilder()
                .setShortCode(shortCode)
                .build();

        return stub.getOriginalUrl(request);
    }

    public GetUrlStatsResponse getUrlStats(String shortCode) {
        GetUrlStatsRequest request = GetUrlStatsRequest.newBuilder()
                .setShortCode(shortCode)
                .build();

        return stub.getUrlStats(request);
    }

    public DeleteUrlResponse deleteUrl(String shortCode) {
        DeleteUrlRequest request = DeleteUrlRequest.newBuilder()
                .setShortCode(shortCode)
                .build();

        return stub.deleteUrl(request);
    }

    // This method is responsible for gracefully shutting down the gRPC channel.
    // It's important to call shutdown() when the application no longer needs the gRPC connection to release resources.
    public void shutdown() {
        channel.shutdown();
    }

}
