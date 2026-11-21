
package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

public class IniciarSesion implements Task {

    private final String usuario;
    private final String clave;

    public IniciarSesion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public static IniciarSesion conCredenciales(
            String usuario, String clave) {
        return new IniciarSesion(usuario, clave);
    }

    @Override
    @Step("{0} inicia sesión con el usuario #usuario")
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", usuario,
                                        "password", clave
                                ))
                        )
        );

        String token = SerenityRest.lastResponse()
                .jsonPath()
                .getString("accessToken");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "No se pudo obtener el accessToken"
            );
        }

        actor.remember("accessToken", token);
    }
}
