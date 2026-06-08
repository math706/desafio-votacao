package com.votacao.service;

import com.votacao.exception.CpfInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CpfValidacaoService")
class CpfValidacaoServiceTest {

    private final CpfValidacaoService service = new CpfValidacaoService();

    @Test
    @DisplayName("deve lançar exceção para CPF com menos de 11 dígitos")
    void deveLancarExcecaoParaCpfCurto() {
        assertThatThrownBy(() -> service.consultarStatus("123"))
            .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    @DisplayName("deve lançar exceção para CPF com todos dígitos iguais")
    void deveLancarExcecaoParaCpfComDigitosIguais() {
        assertThatThrownBy(() -> service.consultarStatus("11111111111"))
            .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    @DisplayName("deve lançar exceção para CPF com dígitos verificadores inválidos")
    void deveLancarExcecaoParaCpfComDigitosVerificadoresInvalidos() {
        assertThatThrownBy(() -> service.consultarStatus("12345678900"))
            .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    @DisplayName("deve lançar exceção para CPF vazio")
    void deveLancarExcecaoParaCpfVazio() {
        assertThatThrownBy(() -> service.consultarStatus(""))
            .isInstanceOf(CpfInvalidoException.class);
    }

    @RepeatedTest(10)
    @DisplayName("deve retornar ABLE_TO_VOTE ou UNABLE_TO_VOTE para CPF válido")
    void deveRetornarStatusParaCpfValido() {
        // CPF válido: 529.982.247-25
        String status = service.consultarStatus("52998224725");
        assertThat(status).isIn(CpfValidacaoService.ABLE_TO_VOTE, CpfValidacaoService.UNABLE_TO_VOTE);
    }
}
