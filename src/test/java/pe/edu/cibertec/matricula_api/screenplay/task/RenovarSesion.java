package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

public class RenovarSesion implements Task {

    public static RenovarSesion tokens() {
        return new RenovarSesion();
    }

    @Override
    @Step("{0} renueva su sesión utilizando el refresh token")
    public <T extends Actor> void performAs(T actor) {

        String refreshAnterior = actor.recall("refreshToken");

        actor.remember("refreshTokenAnterior", refreshAnterior);

        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "refreshToken", refreshAnterior
                                ))
                        )
        );

        String accessNuevo = SerenityRest.lastResponse()
                .jsonPath()
                .getString("accessToken");

        String refreshNuevo = SerenityRest.lastResponse()
                .jsonPath()
                .getString("refreshToken");

        if (accessNuevo == null || accessNuevo.isBlank()) {
            throw new IllegalStateException(
                    "No se obtuvo el nuevo accessToken"
            );
        }

        if (refreshNuevo == null || refreshNuevo.isBlank()) {
            throw new IllegalStateException(
                    "No se obtuvo el nuevo refreshToken"
            );
        }

        actor.remember("accessTokenNuevo", accessNuevo);
        actor.remember("refreshTokenNuevo", refreshNuevo);

        actor.remember("accessToken", accessNuevo);
        actor.remember("refreshToken", refreshNuevo);
    }
}