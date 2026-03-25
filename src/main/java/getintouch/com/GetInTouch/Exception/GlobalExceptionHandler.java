package getintouch.com.GetInTouch.Exception;

import getintouch.com.GetInTouch.DTO.Auth.ApiError;
import getintouch.com.GetInTouch.DTO.Auth.ApiResponseDTO;
import getintouch.com.GetInTouch.Util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ---------- 404 ---------- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {

        ApiError error = new ApiError();
        error.setStatus(404);
        error.setError("NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /* ---------- 400 ---------- */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {

        ApiError error = new ApiError();
        error.setStatus(400);
        error.setError("BAD_REQUEST");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.badRequest().body(error);
    }

    /* ---------- 401 ---------- */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex) {

        ApiError error = new ApiError();
        error.setStatus(401);
        error.setError("UNAUTHORIZED");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /* ---------- Validation Errors ---------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ApiError error = new ApiError();
        error.setStatus(400);
        error.setError("VALIDATION_ERROR");
        error.setMessage(message);
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.badRequest().body(error);
    }

    /* ---------- Fallback (500) ---------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {

        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            throw (org.springframework.security.access.AccessDeniedException) ex;
        }

        ApiError error = new ApiError();
        error.setStatus(500);
        error.setError("INTERNAL_SERVER_ERROR");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleRuntime(RuntimeException ex) {

        return ResponseEntity.badRequest()
                .body(ResponseUtil.failure(ex.getMessage()));
    }


}

