package br.com.fiap.mentalance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("mensagemErro", "A página que você está procurando não foi encontrada.");
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("tituloErro", "Página Não Encontrada");
        return "erro";
    }

    @ExceptionHandler(NegocioException.class)
    public String handleNegocio(NegocioException ex, Model model) {
        model.addAttribute("mensagemErro", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("tituloErro", "Erro na Requisição");
        return "erro";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidacao(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        model.addAttribute("errosValidacao", erros);
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("mensagemErro", "Por favor corrija os campos indicados.");
        model.addAttribute("tituloErro", "Erro de Validação");
        return "erro";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenerico(Exception ex, Model model) {
        model.addAttribute("mensagemErro", "Ocorreu um erro inesperado. Tente novamente.");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("tituloErro", "Erro Interno");
        return "erro";
    }
}

