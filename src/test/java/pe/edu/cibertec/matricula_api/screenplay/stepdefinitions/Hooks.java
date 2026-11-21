package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final String NOMBRE_ESTUDIANTE = "Jimm Michael";
    private static final String CODIGO_ESTUDIANTE = "i202506842";

    @Before
    public void beforeScenario() {

        System.out.println("**************************************");
        System.out.println("ESTUDIANTE: " + NOMBRE_ESTUDIANTE);
        System.out.println("CODIGO: " + CODIGO_ESTUDIANTE);

        String fechaHora = ZonedDateTime.now(ZoneId.of("America/Lima"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        System.out.println("FECHA Y HORA: " + fechaHora);
        System.out.println("**************************************");

        OnStage.setTheStage(new OnlineCast());
    }
}