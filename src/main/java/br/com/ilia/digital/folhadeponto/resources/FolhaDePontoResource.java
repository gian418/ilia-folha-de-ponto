package br.com.ilia.digital.folhadeponto.resources;

import java.util.Optional;
import javax.validation.Valid;

import br.com.ilia.digital.folhadeponto.services.MomentoService;
import org.openapi.folhadeponto.server.api.V1Api;
import org.openapi.folhadeponto.server.model.Alocacao;
import org.openapi.folhadeponto.server.model.Momento;
import org.openapi.folhadeponto.server.model.Registro;
import org.openapi.folhadeponto.server.model.Relatorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

@RestController
public class FolhaDePontoResource implements V1Api {

    @Autowired
    private MomentoService momentoService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Relatorio> geraRelatorioMensal(String mes) {
        return null;
    }

    @Override
    public ResponseEntity<Alocacao> insereAlocacao(@Valid Alocacao alocacao) {
        return null;
    }

    @Override
    public ResponseEntity<Registro> insereBatida(@Valid Momento momento) {
        momentoService.insert(momento);
        return null;
    }
}
