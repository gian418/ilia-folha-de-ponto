package br.com.ilia.digital.folhadeponto.resources.exceptions;

import br.com.ilia.digital.folhadeponto.services.exceptions.MomentoBatidaException;
import io.swagger.models.Response;
import org.openapi.folhadeponto.server.model.Momento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExcpetionHandler {

    @ExceptionHandler(MomentoBatidaException.class)
    public ResponseEntity<StandardError> momentoBatidaInvalida(MomentoBatidaException e, HttpServletRequest request) {
        StandardError err = new StandardError(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }
}
