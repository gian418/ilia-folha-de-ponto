package br.com.ilia.digital.folhadeponto.services;

import br.com.ilia.digital.folhadeponto.models.MomentoVO;
import br.com.ilia.digital.folhadeponto.repository.MomentoRepository;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.openapi.folhadeponto.server.model.Relatorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private MomentoRepository momentoRepository;

    @Autowired
    private MomentoService momentoService;

    @Autowired
    private AlocacaoService alocacaoService;

    public void gerarRelatorio(String anoMes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth anoMesYM = YearMonth.parse(anoMes, formatter);

        LocalDate inicioMes = anoMesYM.atDay(1);
        LocalDate finalMes = anoMesYM.atEndOfMonth();
        List<MomentoVO> momentosMes = momentoRepository.findAllDataBetween(inicioMes, finalMes);

        Relatorio relatorio = new Relatorio();
        relatorio.setRegistros(new ArrayList<>());
        momentosMes.forEach(momentoVO -> {
            relatorio.getRegistros().add(momentoService.construirRegistro(momentoVO.getDataHora().toLocalDate()));
            relatorio.setHorasTrabalhadas(obterTotalHorasTrabalhadasDoDia(momentoVO));
        });

    }

    private String obterTotalHorasTrabalhadasDoDia(MomentoVO momento) {
        Duration totalTrabalhado = alocacaoService.obterDuracaoTotalLancada(momento.getDataHora().toLocalDate());
        String totalTrabalhadoFormatado = DurationFormatUtils.formatDuration(totalTrabalhado.toMillis(), "'PT'HH'H'mm'M'ss'S'");
        return totalTrabalhadoFormatado;
    }
}
