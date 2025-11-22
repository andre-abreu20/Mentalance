package br.com.fiap.mentalance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public String handleNegocio(NegocioException ex, Model model) {
        model.addAttribute("mensagemErro", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        return "erro";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidacao(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        model.addAttribute("errosValidacao", erros);
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("mensagemErro", "Por favor corrija os campos indicados.");
        return "erro";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenerico(Exception ex, Model model) {
        model.addAttribute("mensagemErro", "Ocorreu um erro inesperado. Tente novamente.");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "erro";
    }
}

