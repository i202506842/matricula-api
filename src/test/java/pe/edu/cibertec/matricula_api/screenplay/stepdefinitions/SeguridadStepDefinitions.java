package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarSesion;
import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

public class SeguridadStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Given("que el usuario inicia sesión correctamente")
    public void elUsuarioIniciaSesionCorrectamente() {
        theActorCalled("usuario")
                .whoCan(CallAnApi.at(baseUrl));

        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.conCredenciales(
                        "i202610002",
                        "Cibertec2026"
                )
        );
    }

    @Given("que el alumno inicia sesión correctamente")
    public void elAlumnoIniciaSesionCorrectamente() {
        theActorCalled("alumno")
                .whoCan(CallAnApi.at(baseUrl));

        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.conCredenciales(
                        "i202610002",
                        "Cibertec2026"
                )
        );
    }

    @When("renueva su sesión utilizando el refresh token")
    public void renuevaSuSesion() {
        theActorInTheSpotlight().attemptsTo(
                RenovarSesion.tokens()
        );
    }

    @Then("obtiene un nuevo access token y refresh token")
    public void obtieneNuevosTokens() {
        String accessToken = theActorInTheSpotlight()
                .recall("accessToken");

        String refreshToken = theActorInTheSpotlight()
                .recall("refreshToken");

        if (accessToken == null || accessToken.isBlank()) {
            throw new AssertionError("No se obtuvo el nuevo access token");
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AssertionError("No se obtuvo el nuevo refresh token");
        }
    }

    @Then("el refresh token anterior ya no es válido")
    public void refreshAnteriorNoEsValido() {
        String refreshAnterior = theActorInTheSpotlight()
                .recall("refreshTokenAnterior");

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "refreshToken",
                                        refreshAnterior
                                ))
                        )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(401);
    }

    @When("cierra sesión en la API")
    public void cierraSesion() {
        String token = theActorInTheSpotlight()
                .recall("accessToken");

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/auth/logout")
                        .with(request -> request
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                        )
        );
    }

    @Then("el access token anterior queda invalidado")
    public void accessTokenAnteriorInvalidado() {
        SerenityRest.lastResponse()
                .then()
                .statusCode(204);
    }

    @Then("no puede consultar su perfil con dicho token")
    public void noPuedeConsultarPerfil() {
        String token = theActorInTheSpotlight()
                .recall("accessToken");

        theActorInTheSpotlight().attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(request -> request
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                        )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(401);
    }

    @Given("que el alumno {string} tiene su cuenta habilitada")
    public void alumnoTieneCuentaHabilitada(String alumno) {
        theActorCalled(alumno)
                .whoCan(CallAnApi.at(baseUrl));
    }

    @When("intenta iniciar sesión {int} veces con una contraseña incorrecta")
    public void intentaLoginIncorrecto(int cantidad) {
        String usuario = theActorInTheSpotlight().getName();

        for (int i = 0; i < cantidad; i++) {
            theActorInTheSpotlight().attemptsTo(
                    Post.to("/api/auth/login")
                            .with(request -> request
                                    .contentType(ContentType.JSON)
                                    .body(Map.of(
                                            "username", usuario,
                                            "password", "ClaveIncorrecta123"
                                    ))
                            )
            );
        }
    }

    @Then("su cuenta queda bloqueada")
    public void cuentaQuedaBloqueada() {
        String usuario = theActorInTheSpotlight().getName();

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/auth/login")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", usuario,
                                        "password", "Cibertec2026"
                                ))
                        )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(423);
    }

    @Then("no puede iniciar sesión con su contraseña correcta")
    public void noPuedeLoginCorrecto() {
        String usuario = theActorInTheSpotlight().getName();

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/auth/login")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", usuario,
                                        "password", "Cibertec2026"
                                ))
                        )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(423);
    }

    @When("intenta consultar la lista completa de alumnos")
    public void consultaListaAlumnos() {
        String token = theActorInTheSpotlight()
                .recall("accessToken");

        theActorInTheSpotlight().attemptsTo(
                Get.resource("/api/alumnos")
                        .with(request -> request
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                        )
        );
    }

    @Then("no tiene permisos para acceder a la información de otros alumnos")
    public void noTienePermisosAlumnos() {
        SerenityRest.lastResponse()
                .then()
                .statusCode(403);
    }
}