
package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class PagarMatricula implements Task {

    private final long id;
    private final String metodo;

    public PagarMatricula(long id, String metodo) {
        this.id = id;
        this.metodo = metodo;
    }

    public static PagarMatricula conId(long id, String metodo) {
        return new PagarMatricula(id, metodo);
    }

    @Override
    @Step("{0} paga la matricula #id mediante #metodo")
    public <T extends Actor> void performAs(T actor) {

        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/pagar")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token)
                                .pathParam("id", id)
                                .contentType(ContentType.JSON)
                                .body(Map.of("metodo", metodo))
                        )
        );
    }
}
