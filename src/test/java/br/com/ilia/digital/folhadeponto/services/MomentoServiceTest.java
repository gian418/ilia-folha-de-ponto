package br.com.ilia.digital.folhadeponto.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static br.com.ilia.digital.folhadeponto.enums.MensagensErro.*;

import br.com.ilia.digital.folhadeponto.enums.TipoMomento;
import br.com.ilia.digital.folhadeponto.models.MomentoVO;
import br.com.ilia.digital.folhadeponto.services.exceptions.MomentoBatidaException;
import org.junit.jupiter.api.Test;
import org.openapi.folhadeponto.server.model.Momento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
public class MomentoServiceTest {

    @Autowired
    private MomentoService momentoService;

    @Test
    public void deveRetornarExceptionAoTentarInserirMaisQueQuatroRegistros() {
        Exception exception = assertThrows(MomentoBatidaException.class, () -> {
            Momento momento = new Momento();
            momento.setDataHora("2020-11-16T19:00:00");
            momentoService.validarSalvar(momento);
        });

        String mensagemEsperada = "Momento inválido. Número máximo de 4 batidas atingido";
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsperada));
    }

    @Test
    public void deveRetornarExceptionAoTentarInserirBatidaNoSabado() {
        Exception exception = assertThrows(MomentoBatidaException.class, () -> {
            Momento momento = new Momento();
            momento.setDataHora("2020-11-14T08:00:00");
            momentoService.validarSalvar(momento);
        });

        String mensagemEsperada = ERRO_MSG_MOMENTO_BATIDAS_EM_FINALDESEMANA.getMsg();
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsperada));
    }

    @Test
    public void deveRetornarExceptionAoTentarInserirBatidaNoDomingo() {
        Exception exception = assertThrows(MomentoBatidaException.class, () -> {
            Momento momento = new Momento();
            momento.setDataHora("2020-11-15T08:00:00");
            momentoService.validarSalvar(momento);
        });

        String mensagemEsperada = ERRO_MSG_MOMENTO_BATIDAS_EM_FINALDESEMANA.getMsg();
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsperada));
    }

    @Test
    public void deveRetornarExceptionAoTentarInserirIntervalorMenorQue60Minutos() {
        Exception exception = assertThrows(MomentoBatidaException.class, () -> {
            Momento momento = new Momento();
            momento.setDataHora("2020-11-17T12:59:00");
            momentoService.validarSalvar(momento);
        });

        String mensagemEsperada = ERRO_MSG_MOMENTO_BATIDA_INTERVALO_INVALIDO.getMsg();
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsperada));
    }

    @Test
    public void deveRetornarExceptionAoTentarInserirBatidaComHoraMenorQueUltima() {
        Exception exception = assertThrows(MomentoBatidaException.class, () -> {
            Momento momento = new Momento();
            momento.setDataHora("2020-11-17T10:00:00");
            momentoService.validarSalvar(momento);
        });

        String mensagemEsperada = ERRO_MSG_MOMENTO_BATIDA_HORA_MAIOR_QUE_ULTIMO_REGISTRO.getMsg();
        String mensagemAtual = exception.getMessage();

        assertTrue(mensagemAtual.contains(mensagemEsperada));
    }

    @Test
    public void deveInserirComoEntradaAPrimeiraBatidaDoDia() {
        Momento momento = new Momento();
        momento.setDataHora("2020-11-18T08:00:00");
        momentoService.validarSalvar(momento);

        List<MomentoVO> momentos = momentoService.findAllByData(LocalDate.of(2020, 11, 18));
        Integer quantidadeRegistros = momentos.size();
        Integer quantidadeEsperada = 1;
        TipoMomento tipoRegistro = momentos.get(0).getTipo();

        assertTrue(quantidadeEsperada.equals(quantidadeRegistros));
        assertTrue(tipoRegistro.equals(TipoMomento.ENTRADA));
    }

    @Test
    public void deveInserirComoSaidaASegundaBatidaDoDia() {
        Momento momento = new Momento();
        momento.setDataHora("2020-11-19T12:00:00");
        momentoService.validarSalvar(momento);

        List<MomentoVO> momentos = momentoService.findAllByData(LocalDate.of(2020, 11, 19));
        MomentoVO ultimoMomentoRegistrado = momentos.stream().max(Comparator.comparing(MomentoVO::getDataHora)).get();

        Integer quantidadeEsperada = 2;
        Integer quantidadeRegistros = momentos.size();
        TipoMomento tipoRegistrado = ultimoMomentoRegistrado.getTipo();

        assertTrue(quantidadeEsperada.equals(quantidadeRegistros));
        assertTrue(tipoRegistrado.equals(TipoMomento.SAIDA));
    }

    @Test
    public void deveInserirComoEntradaATerceiraBatidaDoDia() {
        Momento momento = new Momento();
        momento.setDataHora("2020-11-23T13:00:00");
        momentoService.validarSalvar(momento);

        List<MomentoVO> momentos = momentoService.findAllByData(LocalDate.of(2020,11, 23));
        MomentoVO ultimoMomentoRegistrado = momentos.stream().max(Comparator.comparing(MomentoVO::getDataHora)).get();

        Integer quantidadeEsparada = 3;
        Integer quantidadeRegistros = momentos.size();
        TipoMomento tipoRegistrado = ultimoMomentoRegistrado.getTipo();

        assertTrue(quantidadeEsparada.equals(quantidadeRegistros));
        assertTrue(tipoRegistrado.equals(TipoMomento.ENTRADA));
    }

    @Test
    public void deveInserirComoSaidaAQuartaBatidaDoDia() {
        Momento momento = new Momento();
        momento.setDataHora("2020-11-20T18:00:00");
        momentoService.validarSalvar(momento);

        List<MomentoVO> momentos = momentoService.findAllByData(LocalDate.of(2020,11, 20));
        MomentoVO ultimoMomentoRegistrado = momentos.stream().max(Comparator.comparing(MomentoVO::getDataHora)).get();

        Integer quantidadeEsparada = 4;
        Integer quantidadeRegistros = momentos.size();
        TipoMomento tipoRegistrado = ultimoMomentoRegistrado.getTipo();

        assertTrue(quantidadeEsparada.equals(quantidadeRegistros));
        assertTrue(tipoRegistrado.equals(TipoMomento.SAIDA));
    }
}
