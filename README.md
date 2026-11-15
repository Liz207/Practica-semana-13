# Practica-semana-13

<!-- local-development:start -->
## Desarrollo local

Consulta [RUNNING.md](RUNNING.md) para ubicar cada componente, abrirlo en su IDE, compilarlo y conocer las comprobaciones realizadas y los requisitos pendientes.
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
