package br.com.ilia.digital.folhadeponto.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RelatorioServiceTest {

    @Autowired
    private RelatorioService relatorioService;

    @Test
    public void teste1() {
        relatorioService.gerarRelatorio("2020-11");
    }
}
