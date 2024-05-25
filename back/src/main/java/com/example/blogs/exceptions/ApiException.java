package com.example.blogs.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ApiException.class);

    public ApiException(String message) {
        super(message);
        logger.error("ApiException thrown: {}", message);
    }

    public ApiException() {
        super();
        logger.error("ApiException thrown with no message");
    }
}
