package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;

import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.CotizarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.Matricularse;
import pe.edu.cibertec.matricula_api.screenplay.task.PagarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarPagos;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class MatriculaStepDefinitions {

    @When("inicia sesión con el usuario {string} y clave {string}")
    public void iniciaSesion(String usuario, String clave) {

        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.conCredenciales(usuario, clave)
        );

        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200))
        );
    }

    @When("cotiza el curso {string} para el alumno {string}")
    public void cotizaElCurso(String curso, String alumno) {

        theActorInTheSpotlight().attemptsTo(
                CotizarMatricula.para(alumno, curso)
        );
    }

    @Then("la cotizacion contiene {int} creditos, subtotal {string}, descuento {int} y total {string}")
    public void verificarCotizacion(
            int creditos,
            String subtotalTexto,
            int descuento,
            String totalTexto) {

        double subtotalEsperado = Double.parseDouble(subtotalTexto);
        double totalEsperado = Double.parseDouble(totalTexto);

        String subtotalTextoObtenido = SerenityRest.lastResponse()
                .jsonPath()
                .getString("subtotal");

        String totalTextoObtenido = SerenityRest.lastResponse()
                .jsonPath()
                .getString("total");

        if (subtotalTextoObtenido == null || totalTextoObtenido == null) {
            throw new AssertionError(
                    "La respuesta no contiene subtotal o total.\n"
                            + SerenityRest.lastResponse().asString()
            );
        }

        double subtotalObtenido = Double.parseDouble(
                subtotalTextoObtenido.replace("F", "").replace("f", "")
        );

        double totalObtenido = Double.parseDouble(
                totalTextoObtenido.replace("F", "").replace("f", "")
        );

        int creditosObtenidos = SerenityRest.lastResponse()
                .jsonPath()
                .getInt("creditos");

        int descuentoObtenido = SerenityRest.lastResponse()
                .jsonPath()
                .getInt("descuento");

        theActorInTheSpotlight().remember(
                "montoCotizado",
                totalObtenido
        );

        if (creditosObtenidos != creditos) {
            throw new AssertionError(
                    "Créditos incorrectos. Esperados: " + creditos
                            + ", obtenidos: " + creditosObtenidos
            );
        }

        if (Math.abs(subtotalObtenido - subtotalEsperado) > 0.001) {
            throw new AssertionError(
                    "Subtotal incorrecto. Esperado: " + subtotalEsperado
                            + ", obtenido: " + subtotalObtenido
            );
        }

        if (descuentoObtenido != descuento) {
            throw new AssertionError(
                    "Descuento incorrecto. Esperado: " + descuento
                            + ", obtenido: " + descuentoObtenido
            );
        }

        if (Math.abs(totalObtenido - totalEsperado) > 0.001) {
            throw new AssertionError(
                    "Total incorrecto. Esperado: " + totalEsperado
                            + ", obtenido: " + totalObtenido
            );
        }
    }

    @When("se matricula en el curso {string} para el alumno {string}")
    public void seMatricula(String curso, String alumno) {

        Actor actor = theActorInTheSpotlight();

        actor.attemptsTo(
                Matricularse.en(alumno, curso)
        );

        int codigoObtenido = SerenityRest.lastResponse().statusCode();

        if (codigoObtenido != 201) {
            throw new AssertionError(
                    "La matrícula no se registró correctamente.\n"
                            + "Código HTTP esperado: 201\n"
                            + "Código HTTP obtenido: " + codigoObtenido
                            + "\nRespuesta del servidor:\n"
                            + SerenityRest.lastResponse().asString()
            );
        }

        Long id = actor.asksFor(LaRespuesta.idMatricula());

        actor.remember("idMatricula", id);
    }

    @Then("la matricula queda registrada con estado {string} y conserva el monto cotizado")
    public void verificarMatriculaRegistrada(String estado) {

        Actor actor = theActorInTheSpotlight();

        Double montoCotizado = actor.recall("montoCotizado");

        String estadoObtenido = SerenityRest.lastResponse()
                .jsonPath()
                .getString("estado");

        String montoTexto = SerenityRest.lastResponse()
                .jsonPath()
                .getString("monto");

        if (montoCotizado == null) {
            throw new AssertionError(
                    "No se encontró el monto cotizado previamente."
            );
        }

        if (estadoObtenido == null) {
            throw new AssertionError(
                    "La respuesta no contiene el estado de matrícula.\n"
                            + SerenityRest.lastResponse().asString()
            );
        }

        if (montoTexto == null) {
            throw new AssertionError(
                    "La respuesta no contiene el monto de matrícula.\n"
                            + SerenityRest.lastResponse().asString()
            );
        }

        double montoObtenido = Double.parseDouble(
                montoTexto.replace("F", "").replace("f", "")
        );

        if (!estadoObtenido.equals(estado)) {
            throw new AssertionError(
                    "Estado incorrecto. Esperado: " + estado
                            + ", obtenido: " + estadoObtenido
            );
        }

        if (Math.abs(montoObtenido - montoCotizado) > 0.001) {
            throw new AssertionError(
                    "Monto incorrecto. Esperado: " + montoCotizado
                            + ", obtenido: " + montoObtenido
            );
        }
    }

    @When("paga la matricula mediante {string}")
    public void pagaLaMatricula(String metodo) {

        Long id = theActorInTheSpotlight().recall("idMatricula");

        if (id == null) {
            throw new AssertionError(
                    "No se encontró el ID de matrícula para realizar el pago."
            );
        }

        theActorInTheSpotlight().attemptsTo(
                PagarMatricula.conId(id, metodo)
        );
    }

    @Then("la matricula queda pagada con estado {string} y fecha de pago registrada")
    public void verificarPago(String estado) {

        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200))
        );

        theActorInTheSpotlight().should(
                seeThatResponse(
                        "la matrícula fue pagada",
                        response -> response
                                .body("estado", equalTo(estado))
                                .body("fechaPago", notNullValue())
                )
        );
    }

    @When("consulta los pagos del alumno {string} con metodo {string}")
    public void consultaLosPagos(String alumno, String metodo) {

        theActorInTheSpotlight().attemptsTo(
                ConsultarPagos.de(alumno, metodo)
        );
    }

    @Then("el pago de la matricula aparece con metodo {string} y operacion valida")
    public void verificarPagoRegistrado(String metodo) {

        Actor actor = theActorInTheSpotlight();

        actor.should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200))
        );

        Long idMatricula = actor.recall("idMatricula");

        if (idMatricula == null) {
            throw new AssertionError(
                    "No se encontró el ID de matrícula almacenado."
            );
        }

        List<Map<String, Object>> pagos =
                SerenityRest.lastResponse()
                        .jsonPath()
                        .getList("$");

        boolean encontrado = pagos != null && pagos.stream().anyMatch(pago -> {

            Object idPago = pago.get("idMatricula");
            Object numeroOperacion = pago.get("numeroOperacion");

            return idPago instanceof Number
                    && ((Number) idPago).longValue() == idMatricula
                    && metodo.equals(String.valueOf(pago.get("metodo")))
                    && numeroOperacion instanceof String
                    && ((String) numeroOperacion).matches("OP-\\d{6}");
        });

        if (!encontrado) {
            throw new AssertionError(
                    "No se encontró el pago de la matrícula con el método "
                            + metodo
                            + " y un número de operación válido.\n"
                            + "Respuesta del servidor:\n"
                            + SerenityRest.lastResponse().asString()
            );
        }
    }

    @When("intenta matricularse en el curso {string} para el alumno {string}")
    public void intentaMatricularse(String curso, String alumno) {

        theActorInTheSpotlight().attemptsTo(
                Matricularse.en(alumno, curso)
        );
    }
}