package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final Long idMatricula;
    private final double nota;

    public CalificarMatricula(Long idMatricula, double nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula conNota(Long idMatricula, double nota) {
        return new CalificarMatricula(idMatricula, nota);
    }

    @Override
    @Step("{0} registra la nota #nota en la matrícula #idMatricula")
    public <T extends Actor> void performAs(T actor) {

        // Validar ID de matrícula
        if (idMatricula == null) {
            throw new IllegalArgumentException(
                    "El ID de matrícula no puede ser null."
            );
        }

        // Validar nota
        if (nota < 0 || nota > 20) {
            throw new IllegalArgumentException(
                    "La nota debe estar entre 0 y 20. Nota recibida: " + nota
            );
        }

        // Obtener token del docente
        String token = actor.recall("accessToken");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "El actor " + actor.getName()
                            + " no tiene un token de autenticación. "
                            + "Debe iniciar sesión antes de calificar."
            );
        }

        // Enviar nota al API
        actor.attemptsTo(
                Patch.to("/api/matriculas/" + idMatricula + "/nota")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .header("Authorization", "Bearer " + token)
                                .body(Map.of("nota", nota))
                        )
        );
    }
}