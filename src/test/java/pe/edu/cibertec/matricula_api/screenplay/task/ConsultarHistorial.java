package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarHistorial implements Task {

    private final String codigoAlumno;

    public ConsultarHistorial(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public static ConsultarHistorial delAlumno(String codigoAlumno) {
        return new ConsultarHistorial(codigoAlumno);
    }

    @Override
    @Step("{0} consulta el historial académico del alumno #codigoAlumno")
    public <T extends Actor> void performAs(T actor) {

        if (codigoAlumno == null || codigoAlumno.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del alumno no puede estar vacío."
            );
        }

        String token = actor.recall("accessToken");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "El actor " + actor.getName()
                            + " no tiene token de autenticación. "
                            + "Debe iniciar sesión antes de consultar el historial."
            );
        }

        actor.attemptsTo(
                Get.resource("/api/matriculas/historial/" + codigoAlumno)
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}