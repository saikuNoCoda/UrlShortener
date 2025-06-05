package com.example.urlshortner.grpc;

import com.example.urlshortner.model.UrlData;
import com.example.urlshortner.service.UrlShortnerService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

// @GrpcService: This annotation is typically provided by a library like grpc-spring-boot-starter.
// It marks this class as a gRPC service implementation that Spring should manage and register with the gRPC server.
@GrpcService
public class UrlShortnerGrpcService extends UrlShortenerServiceGrpc.UrlShortenerServiceImplBase {

    @Autowired
    private UrlShortnerService urlShortnerService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // Receive a StreamObserver<ResponseType> object (e.g., StreamObserver<ShortenUrlResponse>).
    // This observer is used to send the response back to the client.
    // StreamObserver to send back one or more responses and then complete the call.
    // Send the response to the client using responseObserver.onNext(response).
    // Signal that the RPC call is complete using responseObserver.onCompleted().
    @Override
    public void shortenUrl(ShortenUrlRequest request, StreamObserver<ShortenUrlResponse> responseObserver) {
        try {
            if (request.getOriginalUrl().isEmpty()) {
                responseObserver.onNext(ShortenUrlResponse.newBuilder()
                        .setSuccess(false).setMessage("Original URL cannot be empty")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            if (!isValidUrl(request.getOriginalUrl())) {
                responseObserver.onNext(ShortenUrlResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Invalid Url format")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            System.out.println("We are here!!");
            Long expiration = request.getExpirationSeconds() > 0 ? request.getExpirationSeconds() : null;
            String customeCode = request.getCustomCode().isEmpty() ? null : request.getCustomCode();

            UrlData urlData = urlShortnerService.shortenUrl(request.getOriginalUrl(),
                    customeCode, expiration);
            System.out.println("We are here 2.0!!");


            String shortUrl = baseUrl + "/" + urlData.getShortCode();

            ShortenUrlResponse response = ShortenUrlResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Url shortened successfully")
                    .setShortCode(urlData.getShortCode())
                    .setShortUrl(shortUrl)
                    .setOriginalUrl(urlData.getOriginalUrl())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(ShortenUrlResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getOriginalUrl(GetOriginalUrlRequest request, StreamObserver<GetOriginalUrlResponse> responseObserver) {
        try {
            UrlData urlData = urlShortnerService.getOriginalUrl(request.getShortCode());

            if (urlData != null) {
                GetOriginalUrlResponse response = GetOriginalUrlResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("URL found")
                        .setOriginalUrl(urlData.getOriginalUrl())
                        .setFound(true)
                        .build();
                responseObserver.onNext(response);
            } else {
                GetOriginalUrlResponse response = GetOriginalUrlResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("URL not found or expired")
                        .setFound(false)
                        .build();
                responseObserver.onNext(response);
            }

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(GetOriginalUrlResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .setFound(false)
                    .build());
            responseObserver.onCompleted();
        }
    }


    @Override
    public void getUrlStats(GetUrlStatsRequest request, StreamObserver<GetUrlStatsResponse> responseObserver) {
        try {
            UrlData urlData = urlShortnerService.getUrlStats(request.getShortCode());

            if (urlData != null) {
                GetUrlStatsResponse.Builder responseBuilder = GetUrlStatsResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Stats retrieved successfully")
                        .setOriginalUrl(urlData.getOriginalUrl())
                        .setShortCode(urlData.getShortCode())
                        .setClickCount(urlData.getClickCount())
                        .setCreatedAt(urlData.getCreatedAt());

                if (urlData.getExpiresAt() != null) {
                    responseBuilder.setExpiresAt(urlData.getExpiresAt());
                }

                responseObserver.onNext(responseBuilder.build());
            } else {
                responseObserver.onNext(GetUrlStatsResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("URL not found or expired")
                        .build());
            }

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(GetUrlStatsResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }


    @Override
    public void deleteUrl(DeleteUrlRequest request, StreamObserver<DeleteUrlResponse> responseObserver) {
        try {
            boolean deleted = urlShortnerService.deleteUrl(request.getShortCode());

            DeleteUrlResponse response = DeleteUrlResponse.newBuilder()
                    .setSuccess(deleted)
                    .setMessage(deleted ? "URL deleted successfully" : "URL not found")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(DeleteUrlResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

}
