package com.casestudy.credit.config;

import com.casestudy.credit.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler({BadCredentialsException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleBadCredentials(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("The username or password is incorrect").build();
    }

    @ExceptionHandler({AccountStatusException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleAccountStatus(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("The account is locked").build();
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleAccessDenied(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("You are not authorized to access this resource").build();
    }

    @ExceptionHandler({SignatureException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleSignature(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("The JWT signature is invalid").build();
    }

    @ExceptionHandler({MalformedJwtException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleMalformedJwt(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("The JWT token is malformed").build();
    }

    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleExpiredJwt(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("The JWT token has expired").build();
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleUserNotFound(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error(ex.getMessage()).build();
    }

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage handleUnauthorized(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error(ex.getMessage()).build();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleAll(Exception ex, WebRequest request) {
        return ErrorMessage.builder().error("Unhandled error").build();
    }

}
