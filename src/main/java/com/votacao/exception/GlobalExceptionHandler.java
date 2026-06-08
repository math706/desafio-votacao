package com.votacao.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ApiErro> handlePautaNaoEncontrada(PautaNaoEncontradaException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErro.of(404, "Não encontrado", ex.getMessage()));
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ApiErro> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErro.of(404, "Não encontrado", ex.getMessage()));
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<ApiErro> handleCpfInvalido(CpfInvalidoException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErro.of(404, "Não encontrado", ex.getMessage()));
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ApiErro> handleSessaoEncerrada(SessaoEncerradaException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiErro.of(422, "Sessão encerrada", ex.getMessage()));
    }

    @ExceptionHandler(SessaoJaExisteException.class)
    public ResponseEntity<ApiErro> handleSessaoJaExiste(SessaoJaExisteException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiErro.of(409, "Conflito", ex.getMessage()));
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    public ResponseEntity<ApiErro> handleVotoDuplicado(VotoDuplicadoException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiErro.of(409, "Voto duplicado", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErro> handleValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
        log.warn("Dados inválidos: {}", detalhes);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErro.of(400, "Dados inválidos", "Verifique os campos informados", detalhes));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErro> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Payload inválido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErro.of(400, "Payload inválido", "O corpo da requisição é inválido ou está malformado"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErro> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensagem = "Parâmetro inválido: '" + ex.getName() + "' com valor '" + ex.getValue() + "'";
        log.warn(mensagem);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErro.of(400, "Parâmetro inválido", mensagem));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErro> handleGenerico(Exception ex) {
        log.error("Erro interno inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErro.of(500, "Erro interno", "Ocorreu um erro inesperado. Tente novamente."));
    }
}
