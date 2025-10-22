package com.siewe_rostand.tvcam.shared.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * @author rostand
 * @project tvcam
 */

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
//
//        filterChain.doFilter(request, response);
//
//        logger.info("Outgoing response: {} {}", response.getStatus(), request.getRequestURI());
//    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap the request and response to cache the content
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Log incoming request details BEFORE passing to chain
        logger.info("INCOMING: {} {} from {}",
                wrappedRequest.getMethod(),
                wrappedRequest.getRequestURI(),
                wrappedRequest.getRemoteAddr());

        request.setAttribute("startTime", System.currentTimeMillis());
        // The service/controller reads the body from the wrapper, which caches it.
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        // Calculate processing time (a useful metric!)
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        logger.info("OUTGOING: Status: {} URL: {} TIME:({}ms). Payload: {}",
                wrappedResponse.getStatus(),
                wrappedRequest.getRequestURI(),
                duration,
                getRequestBody(wrappedRequest));

        //IMPORTANT: Copy the response body back to the servlet's output stream
        wrappedResponse.copyBodyToResponse();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) throws UnsupportedEncodingException {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            // Be mindful of large payloads; truncate for logging
            String body = new String(content, request.getCharacterEncoding());
            return body.substring(0, Math.min(body.length(), 500));
        }
        return "N/A";
    }
}
