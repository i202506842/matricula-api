# language: es

Característica: Calificación de matrículas e historial académico
  Como docente
  Quiero registrar las notas de mis alumnos
  Para actualizar su historial académico

  Escenario: El docente registra una nota y el historial se actualiza
    Dado que el docente "D0001" inicia sesión
    Cuando registra la nota 10 al alumno "i202610005" en el curso "PI5620"
    Entonces la matrícula queda calificada con nota 10
    Y el historial del alumno "i202610005" se actualiza
    Y el historial cumple el esquema "historial.json"
    Y el historial muestra condición "DESAPROBADO", créditos aprobados 7 y promedio ponderado "12.92"

  Escenario: Un docente que no dicta el curso intenta registrar una nota
    Dado que el docente "D0002" inicia sesión
    Cuando intenta registrar la nota 15 al alumno "i202610005" en el curso "PI5620"
    Entonces el código de respuesta es 403

  Escenario: No se puede calificar una matrícula no pagada
    Dado que el docente "D0001" inicia sesión
    Cuando intenta registrar la nota 15 al alumno "i202610005" en el curso "PS5511"
    Entonces el código de respuesta es 409