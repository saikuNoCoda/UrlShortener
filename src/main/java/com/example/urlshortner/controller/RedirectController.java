package com.example.urlshortner.controller;

import com.example.urlshortner.model.UrlData;
import com.example.urlshortner.service.UrlShortnerService;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

// @Controller annotation marks the RedirectController class as a Spring MVC controller.
// This means Spring will scan this class for request handling methods and register them to respond
// to incoming web requests.
@Controller
public class RedirectController {

    @Autowired
    private UrlShortnerService urlShortnerService;

    // @GetMapping: This is a Spring MVC annotation that maps HTTP GET requests to a specific handler method.
    // The {} curly braces denote a path variable. This means that whatever value appears in this position
    // in the URL will be extracted and passed as an argument to the method.
    @GetMapping("/{shortCode}")
    // @PathVariable: This annotation tells Spring to bind the value of the URI path variable named shortCode
    // (from the @GetMapping annotation) to the shortCode parameter of this method.
    // ExpiresFilter.XHttpServletResponse response parameter represents the HTTP response object.
    // It allows the controller to directly control the response sent back to the client, such as setting headers,
    // sending redirects, or sending error codes.
    public void redirect(@PathVariable String shortCode, ExpiresFilter.XHttpServletResponse response) throws IOException {
        UrlData urlData = urlShortnerService.getOriginalUrl(shortCode);

        if (urlData != null) {
            response.sendRedirect(urlData.getOriginalUrl());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Short URL not found or expired");
        }
    }
}
