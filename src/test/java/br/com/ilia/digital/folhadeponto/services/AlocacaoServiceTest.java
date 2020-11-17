package br.com.ilia.digital.folhadeponto.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static br.com.ilia.digital.folhadeponto.enums.MensagensErro.*;

import br.com.ilia.digital.folhadeponto.models.AlocacaoVO;
import br.com.ilia.digital.folhadeponto.services.exceptions.AlocacaoException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openapi.folhadeponto.server.model.Alocacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class AlocacaoServiceTest {

    @Autowired
    private AlocacaoService alocacaoService;

    @Test
    public void deveRetornarExcpetionAoTentarInserirAlocacaoUltrapassandoTempoTotalTrabalhado() {
        Exception exception = assertThrows(AlocacaoException.class, () -> {
            Alocacao alocacao = novaAlocacao(LocalDate.of(2020, 11, 16), "PT08H30M00S");
            alocacaoService.validarSalvar(alocacao);
        });

        String mensagemEsparada = ERRO_MSG_ALOCACAO_TEMPO_TOTAL_MAIOR_QUE_BATIDAS.getMsg();
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsparada));
    }

    @Test
    public void deveRetornarExcpetionAoTentarInserirSemTerQuatroBatidasNoDia() {
        Exception exception = assertThrows(AlocacaoException.class, () -> {
           Alocacao alocacao = novaAlocacao(LocalDate.of(2020, 11, 25), "PT01H00M00S");
           alocacaoService.validarSalvar(alocacao);
        });

        String mensagemEsparada = ERRO_MSG_ALOCACAO_QUANTIDADE_BATIDAS_INVALIDA.getMsg();
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsparada));
    }

    @Order(1)
    @Test
    public void deveInserirPrimeiraAlocacaoDentroDoTempoPermitido() {
        Alocacao alocacao = novaAlocacao(LocalDate.of(2020, 11, 24), "PT04H00M00S");
        alocacaoService.validarSalvar(alocacao);

        List<AlocacaoVO> alocacoes = alocacaoService.findAllByDia(alocacao.getDia());
        Duration tempoTotalAlocacoesSalvas = alocacaoService.obterTempoTotalAlocacoesSalvas(alocacoes);
        Duration tempoTotalAlocacoesEsperado = Duration.parse("PT04H00M00S");

        assertEquals(tempoTotalAlocacoesEsperado, tempoTotalAlocacoesSalvas);
    }

    @Order(2)
    @Test
    public void deveInserirSegundaAlocacaoDentroDoTempoPermitido() {
        Alocacao alocacao = novaAlocacao(LocalDate.of(2020,11,24), "PT02H00M00S");
        alocacaoService.validarSalvar(alocacao);

        List<AlocacaoVO> alocacoes = alocacaoService.findAllByDia(alocacao.getDia());
        Duration tempoTotalAlocacoesSalvas = alocacaoService.obterTempoTotalAlocacoesSalvas(alocacoes);
        Duration tempoTotalAlocacoesEsperado = Duration.parse("PT06H00M00S");

        assertEquals(tempoTotalAlocacoesEsperado, tempoTotalAlocacoesSalvas);
    }

    private Alocacao novaAlocacao(LocalDate dia, String tempo) {
        Alocacao alocacao = new Alocacao();
        alocacao.setDia(dia);
        alocacao.setNomeProjeto("Teste");
        alocacao.setTempo(tempo);
        return alocacao;
    }
}
