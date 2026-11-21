# language: es

Característica: Gestión del proceso académico de Luis Ramos
  Como estudiante de CIBERTEC
  Necesito consultar el costo de mis cursos y formalizar mi matrícula
  Para completar correctamente mi inscripción del periodo académico

  Antecedentes:
    Dado que Luis consulta la API de matricula
    Cuando inicia sesión con el usuario "i202610002" y clave "Cibertec2026"

  Escenario: Consultar el importe final del curso asignado
    Cuando cotiza el curso "DM5512" para el alumno "i202610002"
    Entonces el codigo de respuesta es 200
    Y la cotizacion contiene 4 creditos, subtotal "480.0", descuento 10 y total "432.0"
    Y la respuesta cumple el esquema "cotizacion.json"

  Escenario: Completar la inscripción y confirmar el pago por transferencia
    Cuando cotiza el curso "DM5512" para el alumno "i202610002"
    Entonces el codigo de respuesta es 200
    Y la cotizacion contiene 4 creditos, subtotal "480.0", descuento 10 y total "432.0"
    Y la respuesta cumple el esquema "cotizacion.json"

    Cuando se matricula en el curso "DM5512" para el alumno "i202610002"
    Entonces la matricula queda registrada con estado "REGISTRADA" y conserva el monto cotizado
    Y la respuesta cumple el esquema "matricula.json"

    Cuando paga la matricula mediante "TRANSFERENCIA"
    Entonces la matricula queda pagada con estado "PAGADA" y fecha de pago registrada

    Cuando consulta los pagos del alumno "i202610002" con metodo "TRANSFERENCIA"
    Entonces el pago de la matricula aparece con metodo "TRANSFERENCIA" y operacion valida

  Escenario: Detectar un segundo intento de inscripción en un curso ya registrado
    Cuando intenta matricularse en el curso "PS5511" para el alumno "i202610002"
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "MATRICULA_DUPLICADA"
    Y la respuesta cumple el esquema "error.json"