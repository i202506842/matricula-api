package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

public class ElHistorial implements Question<Map<String, Object>> {

    @Override
    public Map<String, Object> answeredBy(Actor actor) {
        return SerenityRest.lastResponse().jsonPath().getMap("");
    }

    public static ElHistorial academico() {
        return new ElHistorial();
    }
}