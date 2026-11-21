
# language: es

Característica: Seguridad de la API de matrícula
  Como usuario de la API
  Necesito comprobar los mecanismos de seguridad
  Para garantizar que el acceso y las operaciones estén protegidos

  Escenario: Rotación del refresh token
    Dado que el usuario inicia sesión correctamente
    Cuando renueva su sesión utilizando el refresh token
    Entonces obtiene un nuevo access token y refresh token
    Y el refresh token anterior ya no es válido

  Escenario: Cierre de sesión e invalidación del access token
    Dado que el alumno inicia sesión correctamente
    Cuando cierra sesión en la API
    Entonces el access token anterior queda invalidado
    Y no puede consultar su perfil con dicho token

  Escenario: Bloqueo de cuenta por intentos fallidos
    Dado que el alumno "i202610004" tiene su cuenta habilitada
    Cuando intenta iniciar sesión 5 veces con una contraseña incorrecta
    Entonces su cuenta queda bloqueada
    Y no puede iniciar sesión con su contraseña correcta

  Escenario: Restricción de acceso a la lista de alumnos
    Dado que el alumno inicia sesión correctamente
    Cuando intenta consultar la lista completa de alumnos
    Entonces el codigo de respuesta es 403
    Y no tiene permisos para acceder a la información de otros alumnos
