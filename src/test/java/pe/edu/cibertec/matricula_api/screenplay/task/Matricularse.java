package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class Matricularse implements Task {

    private final String alumno;
    private final String curso;

    public Matricularse(String alumno, String curso) {
        this.alumno = alumno;
        this.curso = curso;
    }

    public static Matricularse en(String alumno, String curso) {
        return new Matricularse(alumno, curso);
    }

    @Override
    @Step("{0} matricula al alumno #alumno en el curso #curso")
    public <T extends Actor> void performAs(T actor) {

        // Validar código del alumno
        if (alumno == null || alumno.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del alumno no puede estar vacío."
            );
        }

        // Validar código del curso
        if (curso == null || curso.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del curso no puede estar vacío."
            );
        }

        // Obtener token
        String token = actor.recall("accessToken");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "El actor " + actor.getName()
                            + " no tiene token de autenticación. "
                            + "Debe iniciar sesión antes de matricularse."
            );
        }

        // Enviar solicitud de matrícula
        actor.attemptsTo(
                Post.to("/api/matriculas")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token)
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "codigoAlumno", alumno,
                                        "codigoCurso", curso
                                ))
                        )
        );
    }
}