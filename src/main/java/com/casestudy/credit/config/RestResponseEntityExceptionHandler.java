package com.casestudy.credit.config;

import com.casestudy.credit.exception.BusinessException;
import com.casestudy.credit.exception.EntityAlreadyExistsException;
import com.casestudy.credit.exception.ErrorMessage;
import com.casestudy.credit.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class, ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFound(final RuntimeException ex, final WebRequest request) {
        return ErrorMessage.builder().error(ex.getMessage()).build();
    }

    @ExceptionHandler({EntityAlreadyExistsException.class, BusinessException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage handleUnprocessableEntity(final RuntimeException ex, final WebRequest request) {
        return ErrorMessage.builder().error(ex.getMessage()).build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return new ResponseEntity<>(ErrorMessage.builder().errors(errors).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleAll(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("Unhandled error").build();
    }

}
