
package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarPagos implements Task {

    private final String alumno;
    private final String metodo;

    public ConsultarPagos(String alumno, String metodo) {
        this.alumno = alumno;
        this.metodo = metodo;
    }

    public static ConsultarPagos de(String alumno, String metodo) {
        return new ConsultarPagos(alumno, metodo);
    }

    @Override
    @Step("{0} consulta los pagos del alumno #alumno")
    public <T extends Actor> void performAs(T actor) {

        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/pagos")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token)
                                .queryParam("alumno", alumno)
                                .queryParam("metodo", metodo)
                        )
        );
    }
}
