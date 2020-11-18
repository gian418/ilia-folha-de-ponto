package br.com.ilia.digital.folhadeponto.services;

import static br.com.ilia.digital.folhadeponto.enums.TipoMomento.*;
import static br.com.ilia.digital.folhadeponto.enums.MensagensErro.*;

import br.com.ilia.digital.folhadeponto.models.AlocacaoVO;
import br.com.ilia.digital.folhadeponto.models.MomentoVO;
import br.com.ilia.digital.folhadeponto.repository.AlocacaoRepository;
import br.com.ilia.digital.folhadeponto.services.exceptions.AlocacaoException;
import org.openapi.folhadeponto.server.model.Alocacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AlocacaoService {

    @Autowired
    private AlocacaoRepository repository;

    @Autowired
    private MomentoService momentoService;

    public void validarSalvar(Alocacao alocacao) {
        AlocacaoVO alocacaoVO = new AlocacaoVO();
        alocacaoVO.setDia(alocacao.getDia());
        alocacaoVO.setProjeto(alocacao.getNomeProjeto());
        alocacaoVO.setTempo(Duration.parse(alocacao.getTempo()));

        validarAlocacao(alocacaoVO);

        repository.save(alocacaoVO);
    }

    private void validarAlocacao(AlocacaoVO alocacaoVO) {
        List<AlocacaoVO> alocacoes = findAllByDia(alocacaoVO.getDia());
        Duration tempoTotalAlocacoesDoDia = alocacaoVO.getTempo().plus(obterTempoTotalAlocacoesSalvas(alocacoes));
        Duration tempoTotalBatidasDoDia = obterDuracaoTotalLancada(alocacaoVO.getDia());

        if (tempoTotalAlocacoesDoDia.compareTo(tempoTotalBatidasDoDia) > 0)
            throw new AlocacaoException(ERRO_MSG_ALOCACAO_TEMPO_TOTAL_MAIOR_QUE_BATIDAS.getMsg());
    }

    public Duration obterTempoTotalAlocacoesSalvas(List<AlocacaoVO> alocacoes) {
        if(alocacoes.isEmpty()) return Duration.ZERO;
        return alocacoes.stream().map(AlocacaoVO::getTempo).reduce(Duration::plus).get();
    }

    public List<AlocacaoVO> findAllByDia(LocalDate dia) {
        return repository.findAllByDia(dia);
    }

    public Duration obterDuracaoTotalLancada(LocalDate data) {
        List<MomentoVO> momentos = momentoService.findAllByData(data);

        if (momentos.size() < 4) {
            throw new AlocacaoException(ERRO_MSG_ALOCACAO_QUANTIDADE_BATIDAS_INVALIDA.getMsg());
        }

        LocalDateTime dataPrimeiraEntrada = momentos.stream().filter(vo -> vo.getTipo().equals(ENTRADA))
                .min(Comparator.comparing(MomentoVO::getDataHora)).get().getDataHora();

        LocalDateTime dataPrimeiraSaida = momentos.stream().filter(vo -> vo.getTipo().equals(SAIDA))
                .min(Comparator.comparing(MomentoVO::getDataHora)).get().getDataHora();

        Duration duracaoPrimeiraMetade = obterDuracaoEntreDatas(dataPrimeiraEntrada, dataPrimeiraSaida);

        LocalDateTime dataSegundaEntrada = momentos.stream().filter(vo -> vo.getTipo().equals(ENTRADA))
                .max(Comparator.comparing(MomentoVO::getDataHora)).get().getDataHora();

        LocalDateTime dataSegundaSaida = momentos.stream().filter(vo -> vo.getTipo().equals(SAIDA))
                .max(Comparator.comparing(MomentoVO::getDataHora)).get().getDataHora();

        Duration duracaoSegundaMetade = obterDuracaoEntreDatas(dataSegundaEntrada, dataSegundaSaida);

        return duracaoPrimeiraMetade.plus(duracaoSegundaMetade);
    }

    private Duration obterDuracaoEntreDatas(LocalDateTime dataInicio, LocalDateTime dataFinal) {
        return Duration.between(dataInicio, dataFinal);
    }
}
