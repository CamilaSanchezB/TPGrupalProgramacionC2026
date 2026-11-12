$ErrorActionPreference = "Stop"

# compila todos los módulos del proyecto y genera MV.exe
& mvn compile

if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

# ejecuta el programa compilado
& mvn exec:java "-Dexec.mainClass=controller.Controlador"
exit $LASTEXITCODE