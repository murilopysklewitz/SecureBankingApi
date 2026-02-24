package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.exceptions.*;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidAccountData;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // HANDLER OF USERS

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.of(
                409,
                "Email already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCpfAlreadyExists(
            CpfAlreadyExistsException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                409,
                "CPF already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ErrorResponse> handleWeakPassword (
            WeakPasswordException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                400,
                "Weak password",
                ex.getMessage(),
                request.getRequestURI()
        );
        return  ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
                "User not found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ErrorResponse> handleUserInactive(
            UserInactiveException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                400,
                "Inactive user",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlocked(
            UserBlockedException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                400,
                "Blocked ser",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidAccountData.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountData(
            InvalidAccountData ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                400,
                "Invalid account data",
                ex.getMessage(),
                request.getRequestURI()
        );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    //  HANDLE ERROR TRANSACTIONS

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionException(
            InvalidTransactionException ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                400,
                "Invalid Transaction",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex,
            HttpServletRequest request
    ){
        ErrorResponse error = ErrorResponse.of(
                500,
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()

        );
        log.error("Unexpected Error {}", String.valueOf(ex));
        return ResponseEntity.internalServerError().body(error);

    }
}
