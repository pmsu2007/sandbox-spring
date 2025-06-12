package com.mingxoop.sandbox.global.api;

import com.mingxoop.sandbox.global.api.response.BaseResponse;
import com.mingxoop.sandbox.global.api.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exceptionHandler(Exception e) {
		log.error("[Exception] 500 : {} ", e.getMessage());
		e.printStackTrace();

		return ResponseEntity
				.status(500)
				.body(BaseResponse.error(ErrorResponse.of(e)));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<BaseResponse<Void>> runtimeExceptionHandler(RuntimeException e) {
		log.error("[RuntimeException] 500 : {} ", e.getMessage());
		e.printStackTrace();

		return ResponseEntity
				.status(500)
				.body( BaseResponse.error(ErrorResponse.of(e)));
	}
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<BaseResponse<Void>> apiExceptionHandler(ApiException e) {
		log.warn("[ApiException] {} : {}", e.getStatus().getHttpStatus().value(), e.getStatus().getMessage());

		return ResponseEntity
				.status(e.getStatus().getHttpStatus().value())
				.body( BaseResponse.error(ErrorResponse.of(e)));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<BaseResponse<Void>> constraintViolationExceptionHandler(ConstraintViolationException e) {
		log.warn("[ConstraintViolationException] 400 : {}", e.getMessage());

		return ResponseEntity
				.status(400)
				.body( BaseResponse.error(ErrorResponse.of(e)));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<BaseResponse<Void>> noHandlerFoundExceptionHandler(NoResourceFoundException e) {
		log.warn("[NoResourceFoundException] 404 : {}", e.getMessage());

		return ResponseEntity
				.status(404)
				.body( BaseResponse.error(ErrorResponse.of(e)));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<BaseResponse<Void>> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
		log.warn("[MissingServletRequestParameterException] 400 : {}", e.getMessage());

		return ResponseEntity
				.status(400)
				.body( BaseResponse.error(ErrorResponse.of(e)));
	}
}