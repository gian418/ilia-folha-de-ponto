package br.com.ilia.digital.folhadeponto.services;

import br.com.ilia.digital.folhadeponto.enums.MensagensErro;
import br.com.ilia.digital.folhadeponto.enums.TipoMomento;
import br.com.ilia.digital.folhadeponto.models.MomentoVO;
import br.com.ilia.digital.folhadeponto.repository.MomentoRepository;
import br.com.ilia.digital.folhadeponto.services.exceptions.MomentoBatidaException;
import org.openapi.folhadeponto.server.model.Momento;
import org.openapi.folhadeponto.server.model.Registro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.stereotype.Service;

import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.SATURDAY;
import static br.com.ilia.digital.folhadeponto.enums.TipoMomento.ENTRADA;
import static br.com.ilia.digital.folhadeponto.enums.TipoMomento.SAIDA;
import static br.com.ilia.digital.folhadeponto.enums.MensagensErro.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MomentoService {

    @Autowired
    private MomentoRepository repository;

    public Registro validarSalvar(Momento momento)  {
        LocalDateTime dataHoraMomentoAtual = this.converterParaLocalDateTime(momento.getDataHora());
        DayOfWeek diaDaSemana = dataHoraMomentoAtual.getDayOfWeek();

        if (diaDaSemana.equals(SATURDAY) || diaDaSemana.equals(SUNDAY)) {
            throw new MomentoBatidaException(ERRO_MSG_MOMENTO_BATIDAS_EM_FINALDESEMANA.getMsg());
        }

        MomentoVO momentoVO = new MomentoVO();
        momentoVO.setDataHora(dataHoraMomentoAtual);
        momentoVO.setTipo(obterTipoMomento(momentoVO.getDataHora()));
        repository.save(momentoVO);

        return construirRegistro(dataHoraMomentoAtual.toLocalDate());
    }

    public List<MomentoVO> findAllByData(LocalDate dataMomento) {
        List<MomentoVO> momentos = repository.findAllByDate(dataMomento);
        return momentos;
    }

    private TipoMomento obterTipoMomento(LocalDateTime dataHoraMomentoAtual)  {
        List<MomentoVO> momentos = findAllByData(dataHoraMomentoAtual.toLocalDate());
        if(momentos.isEmpty()) return ENTRADA;

        MomentoVO ultimoMomento = momentos.stream().max(Comparator.comparing(MomentoVO::getDataHora)).get();
        validarMomento(momentos, ultimoMomento, dataHoraMomentoAtual);

        if (ultimoMomento.getTipo().equals(ENTRADA)) return SAIDA;

        return ENTRADA;
    }

    private void validarMomento(List<MomentoVO> momentos, MomentoVO ultimoMomento, LocalDateTime dataHoraMomentoAtual) {
        if(momentos.size() >= 4)
            throw new MomentoBatidaException(ERRO_MSG_NUMERO_MAX_MOMENTOS_BATIDAS_ATINGINDO.getMsg());

        if(ultimoMomento.getDataHora().isAfter(dataHoraMomentoAtual))
            throw new MomentoBatidaException(ERRO_MSG_MOMENTO_BATIDA_HORA_MAIOR_QUE_ULTIMO_REGISTRO.getMsg());

        if(ultimoMomento.getTipo().equals(TipoMomento.SAIDA)) {
            long minutosDiferenca = ChronoUnit.MINUTES.between(ultimoMomento.getDataHora(), dataHoraMomentoAtual);
            if(minutosDiferenca < 60)
               throw new MomentoBatidaException(ERRO_MSG_MOMENTO_BATIDA_INTERVALO_INVALIDO.getMsg());

        }
    }

    private LocalDateTime converterParaLocalDateTime(String dataIso) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dataIso, formatter);
        return dateTime;
    }

    public Registro construirRegistro(LocalDate dataBatida) {
        List<MomentoVO> momentos = repository.findAllByDate(dataBatida);

        List<String> horasRegistro =  momentos.stream().map(MomentoVO::getDataHora).map(dataHora -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return dataHora.format(formatter);
        }).collect(Collectors.toList());

        Registro registro = new Registro();
        registro.setDia(dataBatida);
        registro.setHorarios(horasRegistro);
        return registro;
    }
}
