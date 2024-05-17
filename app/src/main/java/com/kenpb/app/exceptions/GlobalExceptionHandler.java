package com.kenpb.app.exceptions;


import com.kenpb.app.constants.GeneralResponseEnum;
import com.kenpb.app.dtos.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiResponse apiResponse = new ApiResponse(
                GeneralResponseEnum.FAILED.getStatusCode(),
                GeneralResponseEnum.FAILED.getMessage(),
                errors);

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.FileSizeExceededModuleException.class)
    public ResponseEntity<ApiResponse> handleFileSizeExceededException(ModuleExceptionHandler.FileSizeExceededModuleException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.LENGTH_REQUIRED.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.InvalidFileTypeException.class)
    public ResponseEntity<ApiResponse> handleInvalidFileTypeModuleException(ModuleExceptionHandler.InvalidFileTypeException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.InvalidModuleException.class)
    public ResponseEntity<ApiResponse> handleInvalidModuleException(ModuleExceptionHandler.InvalidModuleException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.JarAlreadyExitException.class)
    public ResponseEntity<ApiResponse> handleJarAlreadyExitModuleException(ModuleExceptionHandler.JarAlreadyExitException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.CONFLICT.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.CONFLICT);
    }



    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.ModuleNotFoundException.class)
    public ResponseEntity<ApiResponse> handleModuleNotFoundModuleException(ModuleExceptionHandler.ModuleNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.ModuleNotRunningException.class)
    public ResponseEntity<ApiResponse> handleModuleNotRunningModuleException(ModuleExceptionHandler.ModuleNotRunningException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.ModuleInUseException.class)
    public ResponseEntity<ApiResponse> handleModuleInUseModuleException(ModuleExceptionHandler.ModuleInUseException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.CONFLICT.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.CONFLICT);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.ModuleAlreadyRunningModuleException.class)
    public ResponseEntity<ApiResponse> handleModuleAlreadyRunningModuleException(ModuleExceptionHandler.ModuleAlreadyRunningModuleException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.ModuleNotLoadedException.class)
    public ResponseEntity<ApiResponse> handleModuleNotLoadedException(ModuleExceptionHandler.ModuleNotLoadedException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.UserRoleNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserRoleNotLoadedException(ModuleExceptionHandler.UserRoleNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModuleExceptionHandler.UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(ModuleExceptionHandler.UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                GeneralResponseEnum.FAILED.getMessage()), HttpStatus.NOT_FOUND);
    }



}
