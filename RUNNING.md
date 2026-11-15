

<!-- local-development:start -->
# Desarrollo local

Puedes abrir este repositorio en VS Code, Antigravity o Sublime Text. El compilador y los servicios necesarios dependen del componente indicado abajo.

## Java / NetBeans

En NetBeans 25, abre la carpeta que contiene `build.xml` como proyecto Ant. Selecciona un JDK igual o posterior al nivel de fuente del proyecto; las compilaciones de esta revisión usan JDK 24.0.2. El asistente busca NetBeans y el JDK y mantiene la configuración de usuario en `.local-build/`.

```powershell
.uild-local.ps1
.\build-local.ps1 -Project 'build.xml' -Target run
```

Si hay varios proyectos, elige el deseado con `-Project`. Puedes indicar `-JavaHome` y `-NetBeansHome`. `-Target test` ejecuta las pruebas que declare el proyecto; un objetivo de pruebas vacío no acredita cobertura.

## Componentes y validación

Inventario y compilaciones locales del 2026-11-15. Una compilación correcta no acredita todavía interacción, compatibilidad de base de datos, instalador ni despliegue.

| Componente | Tipo / destino | Validación |
| --- | --- | --- |
| [build.xml](<build.xml>) | netbeans-ant / 24 | Compila; comprobado el 2026-11-15; ver alcance actualizado abajo |

## Archivos ignorados

`.gitignore` excluye credenciales locales, estado del editor y salidas de las herramientas detectadas. No deja de versionar archivos que ya estaban en Git. Los manifiestos, archivos de bloqueo, recursos fuente y scripts de esquema forman parte del código.
<!-- local-development:end -->

<!-- build-repairs-20261115:start -->
## Reparación y comprobación — 2026-11-15

- BibliotecaDAOImpl implementa el contrato completo de BibliotecaDAO mediante JDBC/H2 local: libros, préstamos, devolución, respaldo y restauración.
- Se estabilizaron los identificadores al reabrir datos y se conservan fechas, estados y campos de los modelos. No exige las credenciales SQL Server de otra PC.

Comprobado:

- Compila con Ant.
- SmokeBiblioteca pasó doce comprobaciones de persistencia, Unicode, reapertura, devolución, respaldo/restauración, integridad referencial y consultas parametrizadas.

Comandos:

```powershell
.\build-local.ps1
.\test-local.ps1
.\build-local.ps1 -Target run
```

Alcance pendiente:

- Los datos se guardan en data. El esquema SQL Server original se conserva como referencia y no se migró automáticamente a H2.

<!-- build-repairs-20261115:end -->
