package br.com.ilia.digital.folhadeponto.enums;

public enum MensagensErro {
    ERRO_MSG_NUMERO_MAX_MOMENTOS_BATIDAS_ATINGINDO("Momento inválido. Número máximo de 4 batidas atingido"),
    ERRO_MSG_MOMENTO_BATIDAS_EM_FINALDESEMANA("Momento invalido. Batida não pode ser registrada em um final de semana."),
    ERRO_MSG_MOMENTO_BATIDA_INTERVALO_INVALIDO("Momento invalido. O intervalo deve ter no minimo 60 minutos."),
    ERRO_MSG_MOMENTO_BATIDA_HORA_MAIOR_QUE_ULTIMO_REGISTRO("Momento inválido. A hora da batida deve superior a última.");

    private String msg;

    MensagensErro(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
