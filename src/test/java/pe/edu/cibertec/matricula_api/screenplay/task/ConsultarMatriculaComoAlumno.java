package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.rest.SerenityRest;

import java.util.List;
import java.util.Map;

public class ConsultarMatriculaComoAlumno implements Task {

    private final String codigoAlumno;
    private final String codigoCurso;

    public ConsultarMatriculaComoAlumno(
            String codigoAlumno,
            String codigoCurso) {

        this.codigoAlumno = codigoAlumno;
        this.codigoCurso = codigoCurso;
    }

    public static ConsultarMatriculaComoAlumno de(
            String alumno,
            String curso) {

        return new ConsultarMatriculaComoAlumno(alumno, curso);
    }

    @Override
    @Step("{0} consulta su matrícula del curso #codigoCurso")
    public <T extends Actor> void performAs(T actor) {

        // Obtener token
        String token = actor.recall("accessToken");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "El actor " + actor.getName()
                            + " no tiene token de autenticación."
            );
        }

        // Consultar matrícula
        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                                .queryParam("alumno", codigoAlumno)
                                .queryParam("curso", codigoCurso)
                        )
        );

        // Validar respuesta HTTP
        int codigoHttp = SerenityRest.lastResponse().statusCode();

        if (codigoHttp != 200) {
            throw new AssertionError(
                    "Error al consultar matrícula.\n"
                            + "Código HTTP: " + codigoHttp
                            + "\nRespuesta:\n"
                            + SerenityRest.lastResponse().asString()
            );
        }

        // Obtener matrículas
        List<Map<String, Object>> matriculas =
                SerenityRest.lastResponse()
                        .jsonPath()
                        .getList("");

        if (matriculas == null || matriculas.isEmpty()) {
            throw new AssertionError(
                    "No se encontró matrícula para el alumno "
                            + codigoAlumno
                            + " en el curso "
                            + codigoCurso
            );
        }

        // Buscar matrícula del curso específico
        Map<String, Object> matriculaEncontrada = matriculas.stream()
                .filter(matricula ->
                        codigoCurso.equals(
                                String.valueOf(matricula.get("codigoCurso"))
                        )
                )
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No se encontró la matrícula del curso "
                                + codigoCurso
                                + " para el alumno "
                                + codigoAlumno
                ));

        // Obtener ID de matrícula
        Object idObjeto = matriculaEncontrada.get("id");

        if (!(idObjeto instanceof Number)) {
            throw new AssertionError(
                    "La matrícula encontrada no contiene un ID válido.\n"
                            + "Matrícula: " + matriculaEncontrada
            );
        }

        Long idMatricula = ((Number) idObjeto).longValue();

        // Guardar ID para utilizarlo posteriormente
        actor.remember("idMatriculaCalificar", idMatricula);
    }
}