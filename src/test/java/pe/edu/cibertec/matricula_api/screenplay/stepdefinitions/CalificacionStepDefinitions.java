package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarMatriculaComoAlumno;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

public class CalificacionStepDefinitions {

    private final String baseUrl = "http://localhost:8080";

    private String codigoDocenteActual;


    @Given("que el docente {string} inicia sesión")
    public void docenteIniciaSesion(String codigoDocente) {

        codigoDocenteActual = codigoDocente;

        theActorCalled(codigoDocente)
                .whoCan(CallAnApi.at(baseUrl));

        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.conCredenciales(
                        codigoDocente,
                        "Cibertec2026"
                )
        );
    }


    @When("registra la nota {double} al alumno {string} en el curso {string}")
    public void registraNota(double nota, String alumno, String curso) {

        Long id = obtenerIdMatricula(alumno, curso);

        theActorCalled(codigoDocenteActual).attemptsTo(
                CalificarMatricula.conNota(id, nota)
        );
    }


    @When("intenta registrar la nota {double} al alumno {string} en el curso {string}")
    public void intentaRegistrarNota(double nota, String alumno, String curso) {

        Long id = obtenerIdMatricula(alumno, curso);

        theActorCalled(codigoDocenteActual).attemptsTo(
                CalificarMatricula.conNota(id, nota)
        );
    }

    private Long obtenerIdMatricula(String alumno, String curso) {

        theActorCalled(alumno)
                .whoCan(CallAnApi.at(baseUrl));

        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.conCredenciales(
                        alumno,
                        "Cibertec2026"
                )
        );

        theActorInTheSpotlight().attemptsTo(
                ConsultarMatriculaComoAlumno.de(alumno, curso)
        );

        Long id = theActorInTheSpotlight()
                .recall("idMatriculaCalificar");

        // Regresar al actor docente
        theActorCalled(codigoDocenteActual);

        return id;
    }


    @Then("la matrícula queda calificada con nota {double}")
    public void matriculaQuedaCalificada(double nota) {

        SerenityRest.lastResponse()
                .then()
                .statusCode(200);

        double notaObtenida = SerenityRest.lastResponse()
                .jsonPath()
                .getDouble("nota");

        if (Math.abs(notaObtenida - nota) > 0.001) {

            throw new AssertionError(
                    "Nota incorrecta. Esperada: "
                            + nota
                            + ", obtenida: "
                            + notaObtenida
            );
        }
    }


    @Then("el historial del alumno {string} se actualiza")
    public void historialSeActualiza(String alumno) {

        theActorCalled(alumno).attemptsTo(
                ConsultarHistorial.delAlumno(alumno)
        );
    }


    @Then("el historial cumple el esquema {string}")
    public void historialCumpleEsquema(String esquema) {

        theActorInTheSpotlight().should(
                seeThatResponse(
                        "el historial cumple el esquema " + esquema,
                        response -> response.body(
                                matchesJsonSchemaInClasspath(
                                        "schemas/" + esquema
                                )
                        )
                )
        );
    }


    @Then("el historial muestra condición {string}, créditos aprobados {int} y promedio ponderado {string}")
    public void verificarHistorial(
            String condicion,
            int creditos,
            String promedioTexto
    ) {

        double promedio = Double.parseDouble(promedioTexto);

        Map<String, Object> historial =
                SerenityRest.lastResponse()
                        .jsonPath()
                        .getMap("");

        Number creditosObtenidos =
                (Number) historial.get("creditosAprobados");

        Number promedioObtenido =
                (Number) historial.get("promedioPonderado");

        List<Map<String, Object>> cursos =
                SerenityRest.lastResponse()
                        .jsonPath()
                        .getList("cursos");

        // Validar que exista PI5620 con la condición esperada
        boolean cursoEncontrado = cursos != null
                && cursos.stream().anyMatch(curso ->
                "PI5620".equals(curso.get("codigoCurso"))
                        && condicion.equals(curso.get("condicion"))
        );

        if (!cursoEncontrado) {
            throw new AssertionError(
                    "No se encontró el curso PI5620 con condición "
                            + condicion
            );
        }

        // Validar créditos aprobados
        if (creditosObtenidos == null
                || creditosObtenidos.intValue() != creditos) {

            throw new AssertionError(
                    "Créditos aprobados incorrectos. Esperados: "
                            + creditos
                            + ", obtenidos: "
                            + creditosObtenidos
            );
        }

        // Validar promedio ponderado
        if (promedioObtenido == null
                || Math.abs(
                promedioObtenido.doubleValue() - promedio
        ) > 0.001) {

            throw new AssertionError(
                    "Promedio ponderado incorrecto. Esperado: "
                            + promedio
                            + ", obtenido: "
                            + promedioObtenido
            );
        }

    }

    @Then("el código de respuesta es {int}")
    public void verificarCodigoRespuesta(int codigoEsperado) {

        int codigoObtenido =
                SerenityRest.lastResponse().statusCode();

        if (codigoObtenido != codigoEsperado) {

            throw new AssertionError(
                    "Código HTTP incorrecto. Esperado: "
                            + codigoEsperado
                            + ", obtenido: "
                            + codigoObtenido
                            + "\nRespuesta del servidor:\n"
                            + SerenityRest.lastResponse().asString()
            );
        }
    }
}