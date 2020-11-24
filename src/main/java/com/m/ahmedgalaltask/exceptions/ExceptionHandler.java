package com.m.ahmedgalaltask.exceptions;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger();


    @org.springframework.web.bind.annotation.ExceptionHandler(RunTimeBusinessException.class)
    @ResponseBody
    public ResponseEntity<String> handleImportProductException(RunTimeBusinessException e, WebRequest requestInfo , HttpServletRequest request) {
       logger.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }



    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<String> handleImportProductException(Throwable e, WebRequest requestInfo , HttpServletRequest request) {
        logger.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
    }
}
