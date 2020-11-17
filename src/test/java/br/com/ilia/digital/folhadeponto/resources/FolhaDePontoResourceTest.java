package br.com.ilia.digital.folhadeponto.resources;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static io.restassured.http.ContentType.JSON;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.google.gson.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapi.folhadeponto.server.model.Momento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class FolhaDePontoResourceTest {

    Gson gson = new Gson();
    private static final String ENDPOINT_V1_BATIDAS = "/v1/batidas";

    @Autowired
    private FolhaDePontoResource resource;

    @BeforeEach
    public void setup() {
        standaloneSetup(this.resource);
    }

    @Test
    public void deveInserirUmaBatidaComSucesso() {
        Momento momento = new Momento();
        momento.setDataHora("2021-11-24T08:00:00");

        given()
                .contentType(JSON)
                .body(gson.toJson(momento))
                .when().post(ENDPOINT_V1_BATIDAS)
                .then().statusCode(OK.value());
    }

    //@Test
    public void naoDeveInserirUmaBatidaNoSabado() {
        Momento momento = new Momento();
        momento.setDataHora("2020-11-14T08:00:00");

        given()
                .contentType(JSON)
                .body(gson.toJson(momento))
                .when().post(ENDPOINT_V1_BATIDAS).then().statusCode(BAD_REQUEST.value());

    }

    //@Test
    public void naoDeveInserirUmaBatidaNoDomingo() {
        Momento momento = new Momento();
        momento.setDataHora("2020-11-15T08:00:00");

        given()
                .contentType(JSON)
                .body(gson.toJson(momento))
                .when().post(ENDPOINT_V1_BATIDAS).then().statusCode(BAD_REQUEST.value());

    }
}
